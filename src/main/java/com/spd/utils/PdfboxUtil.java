package com.spd.utils;

import com.google.zxing.WriterException;
import com.spd.config.PaperlessConfig;
import com.spd.config.PrintConfig;
import com.spd.pojo.dto.Paperless;
import com.spd.pojo.vo.LowValueTagVO;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.time.Instant;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Component
@Slf4j
public class PdfboxUtil {

    @Autowired
    private ZxingUtil zxingUtil;
    @Autowired
    private PrintConfig printConfig;
    @Autowired
    private PaperlessConfig paperlessConfig;

    private ClassLoader classLoader = this.getClass().getClassLoader();

    public String generatePekingLowValueTag(List<LowValueTagVO> data) throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(300, 180); // 72为PDF中每英寸的点数
        try {
            int sequenceNumber = 1; // Initialize sequence number counter
            for (LowValueTagVO item : data) {
                try {
                    PDPage page = new PDPage(pageSize);
                    document.addPage(page);
                    // 创建一个内容流
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    BufferedImage barcodeImage = zxingUtil.generateQRCodeImage((item.getDefNoPkgCode()), 300, 100, 1);
                    PDImageXObject pdImage = JPEGFactory.createFromImage(document, barcodeImage);
                    contentStream.drawImage(pdImage, 90, 2); // 调整位置
                    contentStream.beginText();
                    contentStream.setFont(font, 10);
                    contentStream.newLineAtOffset(265, 155);
                    contentStream.showText(("序：" + sequenceNumber));
                    contentStream.endText();
                    sequenceNumber++;
                    contentStream.beginText();
                    contentStream.setFont(font, 10);
                    ////设置内容
                    contentStream.newLineAtOffset(10, 155);
                    contentStream.showText("物资编码：" + StringEscapeUtils.unescapeJava(item.getVarietieCode()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("定数码：" + ((item.getDefNoPkgCode())));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("商品名称：" + StringEscapeUtils.unescapeJava(item.getVarietieName()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("规格型号：" + StringEscapeUtils.unescapeJava(item.getSpecificationOrType().length() > 20 ? item.getSpecificationOrType().substring(0, 20) + "..." : item.getSpecificationOrType()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("注册证：" + StringEscapeUtils.unescapeJava(item.getApprovalNumber().length() > 25 ? item.getApprovalNumber().substring(0,25) + "..." : item.getApprovalNumber()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("生产商：" + StringEscapeUtils.unescapeJava(item.getManufacturingEntName().length() > 14 ? item.getManufacturingEntName().substring(0,14) + "..." : item.getManufacturingEntName()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("批号：" + StringEscapeUtils.unescapeJava(item.getBatch()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("效期：" + StringEscapeUtils.unescapeJava(item.getBatchValidityPeriod()));
                    //设置内容
                    contentStream.endText();
                    contentStream.close();
                } catch (IOException | WriterException e) {
                    throw new RuntimeException(e);
                }
            }
            long formattedName = Instant.now().toEpochMilli();
            String locatePath = printConfig.getFileLocation() + "/" + printConfig.getLowValueTagDir() + "/";
            File file = new File(locatePath);
            if(!file.exists()){
                file.mkdirs();
            }
            String filename = formattedName + ".pdf";
            String filepath = locatePath + filename;
            document.save(filepath);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert inputStream != null;
            inputStream.close();
            document.close();
        }
    }


    public String generateLowValueTag(List<LowValueTagVO> data) throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(300,180); // 72为PDF中每英寸的点数
        try {
            for (LowValueTagVO item : data) {
                try {
                    PDPage page = new PDPage(pageSize);
                    document.addPage(page);
                    // 创建一个内容流
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    BufferedImage barcodeImage = zxingUtil.generateCode128((item.getDefNoPkgCode()),250,60,10);
                    PDImageXObject pdImage = JPEGFactory.createFromImage(document, barcodeImage);
                    contentStream.drawImage(pdImage, 30, 110); // 调整位置
                    contentStream.beginText();
                    contentStream.setFont(font, 10);
                    //设置内容
                    contentStream.newLineAtOffset(10, 90);
                    contentStream.showText("物品条码：" + (StringEscapeUtils.unescapeJava(item.getVarietieCode())) + "/" + (StringEscapeUtils.unescapeJava(item.getDefNoPkgCode())));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("系数：" + StringEscapeUtils.unescapeJava(item.getCoefficient()));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("物品名称：" + StringEscapeUtils.unescapeJava(item.getVarietieName().replaceAll("\n","")));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("物品规格：" + StringEscapeUtils.unescapeJava(item.getSpecificationOrType().replaceAll("\n","")));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("批号：" + StringEscapeUtils.unescapeJava(item.getBatch()));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("有效期：" + StringEscapeUtils.unescapeJava(item.getBatchValidityPeriod()));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("供应商：" + StringEscapeUtils.unescapeJava(item.getSupplierName()));
                    //设置内容
                    contentStream.endText();
                    contentStream.close();
                } catch (IOException | WriterException e) {
                    throw new RuntimeException(e);
                }
            }
            long formattedName = Instant.now().toEpochMilli();
            String locatePath = printConfig.getFileLocation() + "/" + printConfig.getLowValueTagDir() + "/";
            File file = new File(locatePath);
            if(!file.exists()){
                file.mkdirs();
            }
            String filename  = formattedName + ".pdf";
            String filepath = locatePath + filename;
            document.save(filepath);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert inputStream != null;
            inputStream.close();
            document.close();
        }
    }
    public File generatePaperlessPDF(Map<String, List<Paperless>> body, int totalLabels, String documentName) throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(595, 841); // A4纸张大小（单位：点）
        try {
            int labelsPerPage = 8; // 每页8个
            int labelsPerColumn = 4; // 每列4个
            float labelWidth = pageSize.getWidth() / 2; // 每个标签宽度为页面宽度的一半

            // 页面上下边距（包含页眉/页脚区域），让间距更紧凑
            float topMargin = 120;
            float bottomMargin = 60;
            float availableHeight = pageSize.getHeight() - topMargin - bottomMargin;
            float labelHeight = availableHeight / labelsPerColumn; // 均分，无多余间隙

            // 二维码缩放
            String defNoPkgCode = "2121123202945"; // 示例定数码
            BufferedImage qrCodeImage = null;
            PDImageXObject pdImage = null;
            try {
                qrCodeImage = zxingUtil.generateQRCodeImage(defNoPkgCode, 100, 100, 1);
                pdImage = JPEGFactory.createFromImage(document, qrCodeImage);
            } catch (WriterException e) {
                throw new RuntimeException("生成二维码失败", e);
            }
            float originalWidth = pdImage.getWidth();
            float originalHeight = pdImage.getHeight();

            int totalPages = Math.max(1, (int) Math.ceil((double) totalLabels / labelsPerPage));
            int printed = 0;

            for (int pageIndex = 1; pageIndex <= totalPages; pageIndex++) {
                PDPage page = new PDPage(pageSize);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // 顶部：logo + 标题 + 基本信息
                InputStream logoStream = classLoader.getResourceAsStream("logo/peking.png");
                if (logoStream != null) {
                    byte[] logoBytes = logoStream.readAllBytes();
                    PDImageXObject logoImage = PDImageXObject.createFromByteArray(document, logoBytes, "logo/peking.png");
                    float logoScaledWidth = logoImage.getWidth() * 0.5f;
                    float logoScaledHeight = logoImage.getHeight() * 0.5f;
                    float pageWidth = pageSize.getWidth();
                    float logoX = (pageWidth - logoScaledWidth) / 2;
                    // 往上移动Logo，减少与表头的重叠
                    contentStream.drawImage(logoImage, logoX, pageSize.getHeight() - 20 - logoScaledHeight, logoScaledWidth, logoScaledHeight);
                }

                // 标题
                contentStream.beginText();
                contentStream.setFont(font, 18);
                float titleWidth = font.getStringWidth("病案条码信息页") / 1000 * 18;
                float titleX = (pageSize.getWidth() - titleWidth) / 2;
                float titleY = pageSize.getHeight() - 30 - 40; // 标题基线Y，上移约30pt
                contentStream.newLineAtOffset(titleX, titleY);
                contentStream.showText("病案条码信息页");
                contentStream.endText();

                // 表头字段（标题下方、分隔线上方）
                contentStream.beginText();
                contentStream.setFont(font, 12);
                float lineY = pageSize.getHeight() - topMargin;
                float headerY = lineY + 10; // 在线条上方10pt
                contentStream.newLineAtOffset(50, headerY);
                contentStream.showText("姓名：");
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("住院号：");
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("次数：");
                contentStream.endText();

                // 分隔线
                contentStream.setLineWidth(1);
                contentStream.moveTo(50, pageSize.getHeight() - topMargin);
                contentStream.lineTo(545, pageSize.getHeight() - topMargin);
                contentStream.stroke();

                // 绘制本页的标签
                int labelsThisPage = Math.min(labelsPerPage, totalLabels - printed);
                for (int i = 0; i < labelsThisPage; i++) {
                    int slot = i; // 0..7
                    int column = slot % 2; // 0左 1右
                    int row = slot / 2; // 0..3 自上而下

                    float cellX = column * labelWidth + 10; // 左右内边距
                    float cellY = pageSize.getHeight() - topMargin - (row + 1) * labelHeight + 10; // 上下内边距
                    float cellW = labelWidth - 20;
                    float cellH = labelHeight - 20;

                    // 标签边框（浅灰色）
                    contentStream.setLineWidth(0.5f);
                    contentStream.setStrokingColor(new Color(200, 200, 200));
                    contentStream.addRect(cellX, cellY, cellW, cellH);
                    contentStream.stroke();
                    // 恢复默认描边颜色用于后续绘制
                    contentStream.setStrokingColor(Color.BLACK);

                    // 计算文本首行位置（更靠上），并放大二维码到右上角与首行齐平
                    float labelFontSize = 9.5f;
                    float textX = cellX + 8;
                    float topY = cellY + cellH - 8; // 内容顶部对齐参考线
                    float enlargedWidth = originalWidth * 0.6f;
                    float enlargedHeight = originalHeight * 0.6f;
                    float imgX = cellX + cellW - enlargedWidth - 8;
                    float imgY = topY - enlargedHeight; // 确保二维码完全在框内
                    contentStream.drawImage(pdImage, imgX, imgY, enlargedWidth, enlargedHeight);

                    // 文本内容（略增字号与行距）
                    float lineGap = 11f;
                    float colGap = 150f; // 同行第二列的水平间距
                    float cursorX = textX;
                    float cursorY = topY - labelFontSize; // 首行

                    contentStream.beginText();
                    contentStream.setFont(font, labelFontSize);
                    contentStream.newLineAtOffset(cursorX, cursorY);
                    contentStream.showText("计费码：175.49");
                    // 下一行显示定数码
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("定数码：" + defNoPkgCode);
                    // 继续下一行
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("物资编码：0187173");
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("产品名称：栓塞保护系统");
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("厂家：威高医疗股份有限公司");
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("生产日期：2022-09-30");
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("有效期：24/07/20");
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("注册证号：国械注准201930306");
                    contentStream.newLineAtOffset(0, -lineGap);
                    // 合并一行：规格型号 + 批号
                    contentStream.showText("规格型号：ACPi-11");
                    contentStream.newLineAtOffset(colGap, 0);
                    contentStream.showText("批号：A0022360227");
                    contentStream.endText();

                    // UDI 自动换行渲染（不拆数字，尽量在标点或空格处断行），在前面9行文本之后续写
                    String udiText = "UDI：(01)C187173(10)526051(11)220930(17)270930(21)526051013687";
                    float maxTextWidth = cellW - 16; // 预留左右内边距
                    List<String> wrapped = wrapTextByWidth(font, labelFontSize, udiText, maxTextWidth);
                    int previousLines = 9; // 计费码、定数码、物资编码、产品名称、厂家、生产日期、有效期、注册证号、规格型号+批号
                    float udiStartY = topY - labelFontSize - previousLines * lineGap; // 严格置于上一行之下
                    float drawY = udiStartY;
                    for (String line : wrapped) {
                        contentStream.beginText();
                        contentStream.setFont(font, labelFontSize);
                        contentStream.newLineAtOffset(textX, drawY);
                        contentStream.showText(line);
                        contentStream.endText();
                        drawY -= lineGap;
                    }
                }
                printed += labelsThisPage;

                // 页脚分隔线
                contentStream.setLineWidth(1);
                contentStream.moveTo(50, bottomMargin);
                contentStream.lineTo(545, bottomMargin);
                contentStream.stroke();

                // 页脚：当前页/总页
                contentStream.beginText();
                contentStream.setFont(font, 8);
                String footerText = pageIndex + " / " + totalPages;
                float footerWidth = font.getStringWidth(footerText) / 1000 * 8;
                float rightX = pageSize.getWidth() - footerWidth - 50;
                contentStream.newLineAtOffset(rightX, bottomMargin - 30);
                contentStream.showText(footerText);
                contentStream.endText();

                contentStream.close();
            }

            // 保存文件
            String locatePath = paperlessConfig.getFileLocation() + "/";
            File file = new File(locatePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String filename = documentName + ".pdf";
            String filepath = locatePath + filename;
            document.save(filepath);
            return new File(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert inputStream != null;
            inputStream.close();
            document.close();
        }
    }

    /**
     * 根据Map的Key循环生成多个PDF文件
     * @param body Map<String, List<Paperless>> 数据
     * @return Map<String, File> 文件名到PDF文件的映射
     * @throws IOException IO异常
     */
    public Map<String, File> generateMultiplePaperlessPDFs(Map<String, List<Paperless>> body) throws IOException {
        Map<String, File> pdfFiles = new HashMap<>();
        
        for (Map.Entry<String, List<Paperless>> entry : body.entrySet()) {
            String documentName = entry.getKey();
            List<Paperless> paperlessList = entry.getValue();
            
            try {
                log.info("开始生成PDF: {}, 数据条数: {}", documentName, paperlessList.size());
                
                // 为每个Key生成对应的PDF文件
                File pdfFile = generatePaperlessPDFForDocument(documentName, paperlessList);
                pdfFiles.put(documentName, pdfFile);
                
                log.info("PDF生成完成: {}, 文件路径: {}", documentName, pdfFile.getAbsolutePath());
                
            } catch (Exception e) {
                log.error("生成PDF失败: {}, 错误信息: {}", documentName, e.getMessage(), e);
                // 继续处理下一个，不中断整个流程
            }
        }
        
        log.info("批量PDF生成完成，共生成 {} 个文件", pdfFiles.size());
        return pdfFiles;
    }

    /**
     * 为指定文档生成PDF文件
     * @param documentName 文档名称
     * @param paperlessList 数据列表
     * @return 生成的PDF文件
     * @throws IOException IO异常
     */
    private File generatePaperlessPDFForDocument(String documentName, List<Paperless> paperlessList) throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(595, 841); // A4纸张大小（单位：点）
        
        try {
            int totalLabels = paperlessList.size();
            int labelsPerPage = 8; // 每页8个
            int labelsPerColumn = 4; // 每列4个
            float labelWidth = pageSize.getWidth() / 2; // 每个标签宽度为页面宽度的一半

            // 页面上下边距（包含页眉/页脚区域），让间距更紧凑
            float topMargin = 120;
            float bottomMargin = 60;
            float availableHeight = pageSize.getHeight() - topMargin - bottomMargin;
            float labelHeight = availableHeight / labelsPerColumn; // 均分，无多余间隙


            int totalPages = Math.max(1, (int) Math.ceil((double) totalLabels / labelsPerPage));
            int printed = 0;

            for (int pageIndex = 1; pageIndex <= totalPages; pageIndex++) {
                PDPage page = new PDPage(pageSize);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // 顶部：logo + 标题 + 基本信息
                InputStream logoStream = classLoader.getResourceAsStream("logo/peking.png");
                if (logoStream != null) {
                    byte[] logoBytes = logoStream.readAllBytes();
                    PDImageXObject logoImage = PDImageXObject.createFromByteArray(document, logoBytes, "logo/peking.png");
                    float logoScaledWidth = logoImage.getWidth() * 0.5f;
                    float logoScaledHeight = logoImage.getHeight() * 0.5f;
                    float pageWidth = pageSize.getWidth();
                    float logoX = (pageWidth - logoScaledWidth) / 2;
                    // 往上移动Logo，减少与表头的重叠
                    contentStream.drawImage(logoImage, logoX, pageSize.getHeight() - 20 - logoScaledHeight, logoScaledWidth, logoScaledHeight);
                }

                // 标题
                contentStream.beginText();
                contentStream.setFont(font, 18);
                float titleWidth = font.getStringWidth("病案条码信息页") / 1000 * 18;
                float titleX = (pageSize.getWidth() - titleWidth) / 2;
                float titleY = pageSize.getHeight() - 30 - 40; // 标题基线Y，上移约30pt
                contentStream.newLineAtOffset(titleX, titleY);
                contentStream.showText("病案条码信息页");
                contentStream.endText();

                // 表头字段（标题下方、分隔线上方）
                contentStream.beginText();
                contentStream.setFont(font, 12);
                float lineY = pageSize.getHeight() - topMargin;
                float headerY = lineY + 10; // 在线条上方10pt
                contentStream.newLineAtOffset(50, headerY);
                contentStream.showText("姓名：" + (paperlessList.isEmpty() ? "" :
                        (paperlessList.get(0).getPatientName() != null ? paperlessList.get(0).getPatientName() : "")));
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("住院号：" + (paperlessList.isEmpty() ? "" : 
                    (paperlessList.get(0).getHospitalizationNumber() != null ? paperlessList.get(0).getHospitalizationNumber() : "")));
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("次数：" + (paperlessList.isEmpty() ? "" : 
                    (paperlessList.get(0).getAdnissTimes() != null ? paperlessList.get(0).getAdnissTimes() : "")));
                contentStream.endText();

                // 分隔线
                contentStream.setLineWidth(1);
                contentStream.moveTo(50, pageSize.getHeight() - topMargin);
                contentStream.lineTo(545, pageSize.getHeight() - topMargin);
                contentStream.stroke();

                // 绘制本页的标签
                int labelsThisPage = Math.min(labelsPerPage, totalLabels - printed);
                for (int i = 0; i < labelsThisPage; i++) {
                    int slot = i; // 0..7
                    int column = slot % 2; // 0左 1右
                    int row = slot / 2; // 0..3 自上而下

                    // 获取当前数据项
                    Paperless currentItem = paperlessList.get(printed + i);

                    float cellX = column * labelWidth + 10; // 左右内边距
                    float cellY = pageSize.getHeight() - topMargin - (row + 1) * labelHeight + 10; // 上下内边距
                    float cellW = labelWidth - 20;
                    float cellH = labelHeight - 20;

                    // 标签边框（浅灰色）
                    contentStream.setLineWidth(0.5f);
                    contentStream.setStrokingColor(new Color(200, 200, 200));
                    contentStream.addRect(cellX, cellY, cellW, cellH);
                    contentStream.stroke();
                    // 恢复默认描边颜色用于后续绘制
                    contentStream.setStrokingColor(Color.BLACK);


                    // 二维码缩放 - 使用第一个数据项的定数码作为示例
                    String defNoPkgCode = currentItem.getDefNoPkgCode() != null ? currentItem.getDefNoPkgCode() : "-";
                    BufferedImage qrCodeImage = null;
                    PDImageXObject pdImage = null;
                    try {
                        qrCodeImage = zxingUtil.generateQRCodeImage(defNoPkgCode, 100, 100, 1);
                        pdImage = JPEGFactory.createFromImage(document, qrCodeImage);
                    } catch (WriterException e) {
                        throw new RuntimeException("生成二维码失败", e);
                    }
                    float originalWidth = pdImage.getWidth();
                    float originalHeight = pdImage.getHeight();

                    // 计算文本首行位置（更靠上），并放大二维码到右上角与首行齐平
                    float labelFontSize = 9.5f;
                    float textX = cellX + 8;
                    float topY = cellY + cellH - 8; // 内容顶部对齐参考线
                    float enlargedWidth = originalWidth * 0.6f;
                    float enlargedHeight = originalHeight * 0.6f;
                    float imgX = cellX + cellW - enlargedWidth - 8;
                    float imgY = topY - enlargedHeight; // 确保二维码完全在框内
                    contentStream.drawImage(pdImage, imgX, imgY, enlargedWidth, enlargedHeight);

                    // 文本内容（略增字号与行距）
                    float lineGap = 11f;
                    float colGap = 150f; // 同行第二列的水平间距
                    float cursorX = textX;
                    float cursorY = topY - labelFontSize; // 首行

                    contentStream.beginText();
                    contentStream.setFont(font, labelFontSize);
                    contentStream.newLineAtOffset(cursorX, cursorY);
                    contentStream.showText("计费码：" + (currentItem.getChargingCode() != null ? currentItem.getChargingCode() : ""));
                    // 下一行显示定数码
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("定数码：" + (currentItem.getDefNoPkgCode() != null ? currentItem.getDefNoPkgCode() : ""));
                    // 继续下一行
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("物资编码：" + (currentItem.getVarietieCodeNew() != null ? currentItem.getVarietieCodeNew() : ""));
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("产品名称：" + (currentItem.getVarietieName() != null ? currentItem.getVarietieName() : ""));
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("厂家：" + (currentItem.getManufacturingEntName() != null ? currentItem.getManufacturingEntName() : ""));
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("生产日期：" + (currentItem.getBatchProductionDate() != null ? currentItem.getBatchProductionDate() : ""));
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("有效期：" + (currentItem.getBatchValidityPeriod() != null ? currentItem.getBatchValidityPeriod() : ""));
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("注册证号：" + (currentItem.getApprovalNumber() != null ? currentItem.getApprovalNumber() : ""));
                    contentStream.newLineAtOffset(0, -lineGap);
                    // 合并一行：规格型号 + 批号
                    contentStream.showText("规格型号：" + (currentItem.getSpecificationOrType() != null ? currentItem.getSpecificationOrType() : ""));
                    contentStream.newLineAtOffset(0, -lineGap);
                    contentStream.showText("批号：" + (currentItem.getBatch() != null ? currentItem.getBatch() : ""));
                    contentStream.endText();

                    // UDI 自动换行渲染（不拆数字，尽量在标点或空格处断行），在前面9行文本之后续写
                    //String udiText = "UDI：-";
                    //float maxTextWidth = cellW - 16; // 预留左右内边距
                    //List<String> wrapped = wrapTextByWidth(font, labelFontSize, udiText, maxTextWidth);
                    //int previousLines = 9; // 计费码、定数码、物资编码、产品名称、厂家、生产日期、有效期、注册证号、规格型号+批号
                    //float udiStartY = topY - labelFontSize - previousLines * lineGap; // 严格置于上一行之下
                    //float drawY = udiStartY;
                    //for (String line : wrapped) {
                    //    contentStream.beginText();
                    //    contentStream.setFont(font, labelFontSize);
                    //    contentStream.newLineAtOffset(textX, drawY);
                    //    contentStream.showText(line);
                    //    contentStream.endText();
                    //    drawY -= lineGap;
                    //}
                }
                printed += labelsThisPage;

                // 页脚分隔线
                contentStream.setLineWidth(1);
                contentStream.moveTo(50, bottomMargin);
                contentStream.lineTo(545, bottomMargin);
                contentStream.stroke();

                // 页脚：当前页/总页
                contentStream.beginText();
                contentStream.setFont(font, 8);
                String footerText = pageIndex + " / " + totalPages;
                float footerWidth = font.getStringWidth(footerText) / 1000 * 8;
                float rightX = pageSize.getWidth() - footerWidth - 50;
                contentStream.newLineAtOffset(rightX, bottomMargin - 30);
                contentStream.showText(footerText);
                contentStream.endText();

                contentStream.close();
            }

            // 保存文件
            String locatePath = paperlessConfig.getFileLocation() + "/" + paperlessConfig.getPaperlessDir() + "/";
            File file = new File(locatePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String filename = documentName + ".pdf";
            String filepath = locatePath + filename;
            document.save(filepath);
            return new File(filepath);

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            document.close();
        }
    }

    // 基于字体像素宽度的软换行，尽量在非数字字符处分行，避免拆数字
    private List<String> wrapTextByWidth(PDFont font, float fontSize, String text, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }
        int start = 0;
        int len = text.length();
        while (start < len) {
            int end = start + 1;
            int bestBreak = -1;
            while (end <= len) {
                String candidate = text.substring(start, end);
                float width = font.getStringWidth(candidate) / 1000f * fontSize;
                if (width <= maxWidth) {
                    // 记录一个更好的分割点：尽量在非数字处（空格、中文标点、英文标点、括号等）
                    char last = candidate.charAt(candidate.length() - 1);
                    if (!Character.isDigit(last) || last == ' ' || last == '-' || last == ')' || last == '(' || last == ':' || last == '/' ) {
                        bestBreak = end;
                    }
                    end++;
                } else {
                    break;
                }
            }
            if (end == start + 1) {
                // 单字符已超宽，强制断行
                lines.add(text.substring(start, end));
                start = end;
            } else if (bestBreak != -1) {
                lines.add(text.substring(start, bestBreak));
                start = bestBreak;
            } else {
                // 没找到理想断点，退一步在允许宽度内的最长子串处断
                lines.add(text.substring(start, end - 1));
                start = end - 1;
            }
        }
        return lines;
    }

}
