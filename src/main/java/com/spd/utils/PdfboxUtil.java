package com.spd.utils;

import com.google.zxing.WriterException;
import com.spd.config.PaperlessConfig;
import com.spd.config.PrintConfig;
import com.spd.pojo.dto.PackageInfo;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private Random random = new Random();

    private Path ensureDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
            return directory;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + directory, e);
        }
    }

    /**
     * 随机干扰定数码，让定数码和二维码不匹配
     * 实现定数码集合内部的错位效果，标签位置不变，但定数码和二维码内容错位
     * @param data 原始数据列表
     * @return 干扰后的数据列表
     */
    public List<LowValueTagVO> randomizeDefNoPkgCode(List<LowValueTagVO> data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        // 深拷贝原始数据，避免修改原始列表
        List<LowValueTagVO> result = new ArrayList<>();
        for (LowValueTagVO originalItem : data) {
            LowValueTagVO newItem = new LowValueTagVO();
            newItem.setDefNoPkgCode(originalItem.getDefNoPkgCode());
            newItem.setManufacturingEntName(originalItem.getManufacturingEntName());
            newItem.setChargingCode(originalItem.getChargingCode());
            newItem.setVarietieName(originalItem.getVarietieName());
            newItem.setSpecificationOrType(originalItem.getSpecificationOrType());
            newItem.setSpecificationOrType2(originalItem.getSpecificationOrType2());
            newItem.setSpecificationOrType3(originalItem.getSpecificationOrType3());
            newItem.setUnit(originalItem.getUnit());
            newItem.setVarietieCode(originalItem.getVarietieCode());
            newItem.setBatch(originalItem.getBatch());
            newItem.setCoefficient(originalItem.getCoefficient());
            newItem.setBatchValidityPeriod(originalItem.getBatchValidityPeriod());
            newItem.setSupplierName(originalItem.getSupplierName());
            newItem.setSUPPLY_PRICE(originalItem.getSUPPLY_PRICE());
            newItem.setApprovalNumber(originalItem.getApprovalNumber());
            result.add(newItem);
        }
        int listSize = result.size();
        
        if (listSize <= 1) {
            return result; // 只有一个或没有元素时不需要错位
        }
        
        // 随机选择1到listSize个元素进行错位
        int interferenceCount = random.nextInt(listSize) + 1;
        
        // 收集所有定数码
        List<String> allDefNoPkgCodes = new ArrayList<>();
        for (LowValueTagVO item : result) {
            allDefNoPkgCodes.add(item.getDefNoPkgCode());
        }
        
        // 随机打乱定数码列表
        Collections.shuffle(allDefNoPkgCodes);
        
        // 随机选择要错位的索引
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        
        // 对选中的元素进行错位
        for (int i = 0; i < interferenceCount; i++) {
            int index = indices.get(i);
            LowValueTagVO item = result.get(index);
            String originalCode = item.getDefNoPkgCode();
            
            // 从打乱的定数码列表中选择一个不同的定数码
            String newCode = null;
            for (String code : allDefNoPkgCodes) {
                if (!code.equals(originalCode)) {
                    newCode = code;
                    break;
                }
            }
            
            if (newCode != null) {
                item.setDefNoPkgCode(newCode);
            }
        }
        
        return result;
    }

    // 保持向后兼容性的重载方法
    public String generatePekingLowValueTag(List<LowValueTagVO> data) throws IOException {
        return generatePekingLowValueTag(data, false);
    }

    public String generatePekingLowValueTag(List<LowValueTagVO> data, boolean isRandom) throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(300, 180); // 72为PDF中每英寸的点数
        try {
            // 如果是random模式，对数据进行随机干扰
            List<LowValueTagVO> processedData = isRandom ? randomizeDefNoPkgCode(data) : data;
            
            int sequenceNumber = 1; // Initialize sequence number counter
            for (int i = 0; i < processedData.size(); i++) {
                LowValueTagVO item = processedData.get(i);
                // 标签显示使用原始数据
                LowValueTagVO originalItem = data.get(i);
                try {
                    PDPage page = new PDPage(pageSize);
                    document.addPage(page);
                    // 创建一个内容流
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    // 二维码使用错位的定数码，显示文本使用原始定数码
                    String qrCodeContent = isRandom ? item.getDefNoPkgCode() : originalItem.getDefNoPkgCode();
                    BufferedImage barcodeImage = zxingUtil.generateQRCodeImage(qrCodeContent, 300, 100, 1);
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
                    contentStream.showText("物资编码：" + StringEscapeUtils.unescapeJava(originalItem.getVarietieCode()));
                    contentStream.newLineAtOffset(0, -19);
                    // 显示文本使用原始定数码
                    contentStream.showText("定数码：" + originalItem.getDefNoPkgCode());
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("商品名称：" + StringEscapeUtils.unescapeJava(originalItem.getVarietieName()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("规格型号：" + StringEscapeUtils.unescapeJava(originalItem.getSpecificationOrType().length() > 20 ? originalItem.getSpecificationOrType().substring(0, 20) + "..." : originalItem.getSpecificationOrType()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("注册证：" + StringEscapeUtils.unescapeJava(originalItem.getApprovalNumber().length() > 25 ? originalItem.getApprovalNumber().substring(0,25) + "..." : originalItem.getApprovalNumber()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("生产商：" + StringEscapeUtils.unescapeJava(originalItem.getManufacturingEntName().length() > 14 ? originalItem.getManufacturingEntName().substring(0,14) + "..." : originalItem.getManufacturingEntName()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("批号：" + StringEscapeUtils.unescapeJava(originalItem.getBatch()));
                    contentStream.newLineAtOffset(0, -19);
                    contentStream.showText("效期：" + StringEscapeUtils.unescapeJava(originalItem.getBatchValidityPeriod()));
                    //设置内容
                    contentStream.endText();
                    contentStream.close();
                } catch (IOException | WriterException e) {
                    throw new RuntimeException(e);
                }
            }
            long formattedName = Instant.now().toEpochMilli();
            Path locatePath = ensureDirectory(Paths.get(printConfig.getFileLocation(), printConfig.getLowValueTagDir()));
            String filename = formattedName + ".pdf";
            Path filePath = locatePath.resolve(filename);
            document.save(filePath.toString());
            return filename;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert inputStream != null;
            inputStream.close();
            document.close();
        }
    }

    // 保持向后兼容性的重载方法
    public String generateLowValueTag(List<LowValueTagVO> data) throws IOException {
        return generateLowValueTag(data, false);
    }

    public String generateLowValueTag(List<LowValueTagVO> data, boolean isRandom) throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(300,180); // 72为PDF中每英寸的点数
        try {
            // 如果是random模式，对数据进行随机干扰
            List<LowValueTagVO> processedData = isRandom ? randomizeDefNoPkgCode(data) : data;
            
            for (int i = 0; i < processedData.size(); i++) {
                LowValueTagVO item = processedData.get(i);
                // 标签显示使用原始数据
                LowValueTagVO originalItem = data.get(i);
                try {
                    PDPage page = new PDPage(pageSize);
                    document.addPage(page);
                    // 创建一个内容流
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    // 条码使用错位的定数码，显示文本使用原始定数码
                    String barcodeContent = isRandom ? item.getDefNoPkgCode() : originalItem.getDefNoPkgCode();
                    String displayText = originalItem.getDefNoPkgCode();
                    BufferedImage barcodeImage = zxingUtil.generateCode128(barcodeContent, displayText, 250, 60, 10);
                    PDImageXObject pdImage = JPEGFactory.createFromImage(document, barcodeImage);
                    contentStream.drawImage(pdImage, 30, 110); // 调整位置
                    contentStream.beginText();
                    contentStream.setFont(font, 10);
                    //设置内容
                    contentStream.newLineAtOffset(10, 90);
                    // 显示文本使用原始定数码
                    contentStream.showText("物品条码：" + (StringEscapeUtils.unescapeJava(originalItem.getVarietieCode())) + "/" + (StringEscapeUtils.unescapeJava(originalItem.getDefNoPkgCode())));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("系数：" + StringEscapeUtils.unescapeJava(originalItem.getCoefficient()));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("物品名称：" + StringEscapeUtils.unescapeJava(originalItem.getVarietieName().replaceAll("\n","")));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("物品规格：" + StringEscapeUtils.unescapeJava(originalItem.getSpecificationOrType().replaceAll("\n","")));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("批号：" + StringEscapeUtils.unescapeJava(originalItem.getBatch()));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("有效期：" + StringEscapeUtils.unescapeJava(originalItem.getBatchValidityPeriod()));
                    contentStream.newLineAtOffset(0, -14);
                    contentStream.showText("供应商：" + StringEscapeUtils.unescapeJava(originalItem.getSupplierName()));
                    //设置内容
                    contentStream.endText();
                    contentStream.close();
                } catch (IOException | WriterException e) {
                    throw new RuntimeException(e);
                }
            }
            long formattedName = Instant.now().toEpochMilli();
            Path locatePath = ensureDirectory(Paths.get(printConfig.getFileLocation(), printConfig.getLowValueTagDir()));
            String filename  = formattedName + ".pdf";
            Path filePath = locatePath.resolve(filename);
            document.save(filePath.toString());
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
            Path locatePath = ensureDirectory(Paths.get(paperlessConfig.getFileLocation()));
            String filename = documentName + ".pdf";
            Path filePath = locatePath.resolve(filename);
            document.save(filePath.toString());
            return filePath.toFile();
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
                contentStream.showText("住院次数：" + (paperlessList.isEmpty() ? "" :
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
            Path locatePath = ensureDirectory(Paths.get(paperlessConfig.getFileLocation(), paperlessConfig.getPaperlessDir()));
            String filename = documentName + ".pdf";
            Path filePath = locatePath.resolve(filename);
            document.save(filePath.toString());
            return filePath.toFile();

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

    /**
     * 生成包含原始码和包码的测试PDF
     * 总共4个原始码数据，生成3页PDF：
     * - 第1页：8个原始码（2列4行，打满页面）
     * - 第2页：5个原始码（左列4个+右列1个）+ 1个包码（右列）
     * - 第3页：5个原始码（左列4个+右列1个）+ 1个包码（右列）
     * @return 生成的PDF文件
     * @throws IOException IO异常
     */
    public File generateTestPDFWithPackageCodes() throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(595, 841); // A4纸张大小（单位：点）
        
        try {
            // 创建4个写死的原始码数据
            List<Paperless> testDataList = createTestPaperlessData();
            
            // 创建4个写死的包码数据
            List<PackageInfo> packageInfoList = createTestPackageData();

            int labelsPerColumn = 4; // 保持与原方法一致，每列4个位置
            float labelWidth = pageSize.getWidth() / 2; // 标签宽度为页面宽度的一半（与原方法一致）

            // 页面上下边距（与原方法保持一致）
            float topMargin = 120;
            float bottomMargin = 60;
            float availableHeight = pageSize.getHeight() - topMargin - bottomMargin;
            float labelHeight = availableHeight / labelsPerColumn; // 均分，与原方法保持一致
            float cellW = labelWidth - 20;
            float cellH = labelHeight - 20;

            int totalPages = 3; // 固定3页

            for (int pageIndex = 1; pageIndex <= totalPages; pageIndex++) {
                PDPage page = new PDPage(pageSize);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // 顶部：logo + 标题 + 基本信息（与原方法完全一致）
                InputStream logoStream = classLoader.getResourceAsStream("logo/peking.png");
                if (logoStream != null) {
                    byte[] logoBytes = logoStream.readAllBytes();
                    PDImageXObject logoImage = PDImageXObject.createFromByteArray(document, logoBytes, "logo/peking.png");
                    float logoScaledWidth = logoImage.getWidth() * 0.5f;
                    float logoScaledHeight = logoImage.getHeight() * 0.5f;
                    float pageWidth = pageSize.getWidth();
                    float logoX = (pageWidth - logoScaledWidth) / 2;
                    contentStream.drawImage(logoImage, logoX, pageSize.getHeight() - 20 - logoScaledHeight, logoScaledWidth, logoScaledHeight);
                }

                // 标题
                contentStream.beginText();
                contentStream.setFont(font, 18);
                float titleWidth = font.getStringWidth("病案条码信息页") / 1000 * 18;
                float titleX = (pageSize.getWidth() - titleWidth) / 2;
                float titleY = pageSize.getHeight() - 30 - 40;
                contentStream.newLineAtOffset(titleX, titleY);
                contentStream.showText("病案条码信息页");
                contentStream.endText();

                // 表头字段（标题下方、分隔线上方）
                contentStream.beginText();
                contentStream.setFont(font, 12);
                float lineY = pageSize.getHeight() - topMargin;
                float headerY = lineY + 10;
                contentStream.newLineAtOffset(50, headerY);
                contentStream.showText("姓名：" + (testDataList.isEmpty() ? "" :
                        (testDataList.get(0).getPatientName() != null ? testDataList.get(0).getPatientName() : "")));
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("住院号：" + (testDataList.isEmpty() ? "" : 
                    (testDataList.get(0).getHospitalizationNumber() != null ? testDataList.get(0).getHospitalizationNumber() : "")));
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("住院次数：" + (testDataList.isEmpty() ? "" :
                    (testDataList.get(0).getAdnissTimes() != null ? testDataList.get(0).getAdnissTimes() : "")));
                contentStream.endText();

                // 分隔线
                contentStream.setLineWidth(1);
                contentStream.moveTo(50, pageSize.getHeight() - topMargin);
                contentStream.lineTo(545, pageSize.getHeight() - topMargin);
                contentStream.stroke();

                if (pageIndex == 1) {
                    // 第1页：显示8个原始码（2列4行，打满页面）
                    for (int i = 0; i < 8; i++) {
                        int column = i % 2; // 0或1
                        int row = i / 2; // 0, 1, 2, 3
                        // 循环使用4个原始码数据
                        int dataIndex = i % testDataList.size();
                        
                        Paperless currentItem = testDataList.get(dataIndex);
                        float cellX = column * labelWidth + 10;
                        float cellY = pageSize.getHeight() - topMargin - (row + 1) * labelHeight + 10;

                        // 卡片边框
                        contentStream.setLineWidth(0.5f);
                        contentStream.setStrokingColor(new Color(200, 200, 200));
                        contentStream.addRect(cellX, cellY, cellW, cellH);
                        contentStream.stroke();
                        contentStream.setStrokingColor(Color.BLACK);

                        // 绘制原始码
                        drawOriginalCodeSection(document, contentStream, font, currentItem, 
                                cellX, cellY, cellW, cellH);
                    }
                } else if (pageIndex == 2) {
                    // 第2页：5个原始码 + 1个包码
                    // 左列：显示4个原始码（row 0, 1, 2, 3）
                    for (int row = 0; row < 4; row++) {
                        int dataIndex = row % testDataList.size();
                        Paperless currentItem = testDataList.get(dataIndex);
                        float cellX = 0 * labelWidth + 10;
                        float cellY = pageSize.getHeight() - topMargin - (row + 1) * labelHeight + 10;

                        // 卡片边框
                        contentStream.setLineWidth(0.5f);
                        contentStream.setStrokingColor(new Color(200, 200, 200));
                        contentStream.addRect(cellX, cellY, cellW, cellH);
                        contentStream.stroke();
                        contentStream.setStrokingColor(Color.BLACK);

                        // 绘制原始码
                        drawOriginalCodeSection(document, contentStream, font, currentItem, 
                                cellX, cellY, cellW, cellH);
                    }
                    
                    // 右列：第1行显示原始码，第2行显示包码
                    // 右列第1行：原始码（第5个原始码）
                    int dataIndex4 = 4 % testDataList.size();
                    Paperless currentItem4 = testDataList.get(dataIndex4);
                    float cellX = 1 * labelWidth + 10;
                    float cellY = pageSize.getHeight() - topMargin - (0 + 1) * labelHeight + 10;

                    // 卡片边框
                    contentStream.setLineWidth(0.5f);
                    contentStream.setStrokingColor(new Color(200, 200, 200));
                    contentStream.addRect(cellX, cellY, cellW, cellH);
                    contentStream.stroke();
                    contentStream.setStrokingColor(Color.BLACK);

                    // 绘制原始码
                    drawOriginalCodeSection(document, contentStream, font, currentItem4, 
                            cellX, cellY, cellW, cellH);
                    
                    // 右列第2行：包码 0
                    if (packageInfoList.size() > 0) {
                        PackageInfo packageInfo = packageInfoList.get(0);
                        float packageCellY = pageSize.getHeight() - topMargin - (1 + 1) * labelHeight + 10;

                        // 卡片边框
                        contentStream.setLineWidth(0.5f);
                        contentStream.setStrokingColor(new Color(200, 200, 200));
                        contentStream.addRect(cellX, packageCellY, cellW, cellH);
                        contentStream.stroke();
                        contentStream.setStrokingColor(Color.BLACK);

                        // 绘制包码
                        drawPackageCodeSection(document, contentStream, font, packageInfo, 
                                cellX, packageCellY, cellW, cellH);
                    }
                } else if (pageIndex == 3) {
                    // 第3页：5个原始码 + 1个包码
                    // 左列：显示4个原始码（row 0, 1, 2, 3）
                    for (int row = 0; row < 4; row++) {
                        // 从索引4开始循环使用数据
                        int dataIndex = (row + 4) % testDataList.size();
                        Paperless currentItem = testDataList.get(dataIndex);
                        float cellX = 0 * labelWidth + 10;
                        float cellY = pageSize.getHeight() - topMargin - (row + 1) * labelHeight + 10;

                        // 卡片边框
                        contentStream.setLineWidth(0.5f);
                        contentStream.setStrokingColor(new Color(200, 200, 200));
                        contentStream.addRect(cellX, cellY, cellW, cellH);
                        contentStream.stroke();
                        contentStream.setStrokingColor(Color.BLACK);

                        // 绘制原始码
                        drawOriginalCodeSection(document, contentStream, font, currentItem, 
                                cellX, cellY, cellW, cellH);
                    }
                    
                    // 右列：第1行显示原始码，第2行显示包码
                    // 右列第1行：原始码（第5个原始码）
                    int dataIndex8 = 8 % testDataList.size();
                    Paperless currentItem8 = testDataList.get(dataIndex8);
                    float cellX = 1 * labelWidth + 10;
                    float cellY = pageSize.getHeight() - topMargin - (0 + 1) * labelHeight + 10;

                    // 卡片边框
                    contentStream.setLineWidth(0.5f);
                    contentStream.setStrokingColor(new Color(200, 200, 200));
                    contentStream.addRect(cellX, cellY, cellW, cellH);
                    contentStream.stroke();
                    contentStream.setStrokingColor(Color.BLACK);

                    // 绘制原始码
                    drawOriginalCodeSection(document, contentStream, font, currentItem8, 
                            cellX, cellY, cellW, cellH);
                    
                    // 右列第2行：包码 1
                    if (packageInfoList.size() > 1) {
                        PackageInfo packageInfo = packageInfoList.get(1);
                        float packageCellY = pageSize.getHeight() - topMargin - (1 + 1) * labelHeight + 10;

                        // 卡片边框
                        contentStream.setLineWidth(0.5f);
                        contentStream.setStrokingColor(new Color(200, 200, 200));
                        contentStream.addRect(cellX, packageCellY, cellW, cellH);
                        contentStream.stroke();
                        contentStream.setStrokingColor(Color.BLACK);

                        // 绘制包码
                        drawPackageCodeSection(document, contentStream, font, packageInfo, 
                                cellX, packageCellY, cellW, cellH);
                    }
                }

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
            Path locatePath = ensureDirectory(Paths.get(paperlessConfig.getFileLocation(), paperlessConfig.getPaperlessDir()));
            String filename = "器械条码与包码_" + System.currentTimeMillis() + ".pdf";
            Path filePath = locatePath.resolve(filename);
            document.save(filePath.toString());
            log.info("测试PDF生成完成: {}", filePath);
            return filePath.toFile();

        } finally {
            document.close();
        }
    }

    /**
     * 创建测试用的原始码数据（使用真实数据）
     */
    private List<Paperless> createTestPaperlessData() {
        List<Paperless> list = new ArrayList<>();

        // 数据1：医用胶
        Paperless p1 = new Paperless();
        p1.setPatientName("张三");
        p1.setHospitalizationNumber("4564408");
        p1.setAdnissTimes("1");
        p1.setChargingCode("740448");  // 计费码，7开头
        p1.setDefNoPkgCode("10025032436048");  // 定数码（物料码），1开头
        p1.setVarietieCodeNew("1413237");
        p1.setVarietieName("医用胶");
        p1.setManufacturingEntName("北京康派特医疗器械有限公司");
        p1.setBatchProductionDate("2024-11-12");
        p1.setBatchValidityPeriod("2026-11-11");
        p1.setApprovalNumber("国械注准20213020696");
        p1.setSpecificationOrType("腔镜型/1.0ml/支");
        p1.setBatch("241112");
        list.add(p1);

        // 数据2：可吸收止血纱布
        Paperless p2 = new Paperless();
        p2.setPatientName("张三");
        p2.setHospitalizationNumber("4564408");
        p2.setAdnissTimes("1");
        p2.setChargingCode("745416");  // 计费码，7开头
        p2.setDefNoPkgCode("10025040246209");  // 定数码（物料码），1开头
        p2.setVarietieCodeNew("1416914");
        p2.setVarietieName("可吸收止血纱布");
        p2.setManufacturingEntName("惠州华阳医疗器械有限公司");
        p2.setBatchProductionDate("2024-10-27");
        p2.setBatchValidityPeriod("2026-10-26");
        p2.setApprovalNumber("国械注准20143142370");
        p2.setSpecificationOrType("100*75mm");
        p2.setBatch("24103371");
        list.add(p2);

        // 数据3：输液接头及管路
        Paperless p3 = new Paperless();
        p3.setPatientName("张三");
        p3.setHospitalizationNumber("4564408");
        p3.setAdnissTimes("1");
        p3.setChargingCode("701682");  // 计费码，7开头
        p3.setDefNoPkgCode("10025033139653");  // 定数码（物料码），1开头
        p3.setVarietieCodeNew("1504484");
        p3.setVarietieName("输液接头及管路");
        p3.setManufacturingEntName("美国ICU Medical, Inc.");
        p3.setBatchProductionDate("2024-03-01");
        p3.setBatchValidityPeriod("2029-02-28");
        p3.setApprovalNumber("国械注进20163140329");
        p3.setSpecificationOrType("01C-C4207/1个/包");
        p3.setBatch("13937369");
        list.add(p3);

        // 数据4：一次性使用有创血压传感器
        Paperless p4 = new Paperless();
        p4.setPatientName("张三");
        p4.setHospitalizationNumber("4564408");
        p4.setAdnissTimes("1");
        p4.setChargingCode("746668");  // 计费码，7开头
        p4.setDefNoPkgCode("10025033141293");  // 定数码（物料码），1开头
        p4.setVarietieCodeNew("1417232");
        p4.setVarietieName("一次性使用有创血压传感器");
        p4.setManufacturingEntName("深圳市惠心诺科技有限公司");
        p4.setBatchProductionDate("2025-02-10");
        p4.setBatchValidityPeriod("2028-02-09");
        p4.setApprovalNumber("国械注准20223070267");
        p4.setSpecificationOrType("HXN12-Ⅰ-03/单头");
        p4.setBatch("H25041324");
        list.add(p4);

        return list;
    }

    /**
     * 创建测试用的包码数据（使用真实数据）
     */
    private List<PackageInfo> createTestPackageData() {
        List<PackageInfo> list = new ArrayList<>();
        
        // 获取今天和14天后的日期
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(14);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        String todayStr = today.format(formatter);
        String expiryDateStr = expiryDate.format(formatter);
        
        // 生成包号条码（00 + yyyyMMdd + 序号）
        DateTimeFormatter barcodeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = today.format(barcodeFormatter);

        list.add(PackageInfo.builder()
                .implantDevice("植入物器械")
                .packager("杨宇")
                .packageNumber("1413237")
                .sterilizationDate(todayStr)
                .expiryDate(expiryDateStr)
                .packageMaterial("医用无纺布")
                .batchNumber("GC" + dateStr + "01")
                .packageBarcode("00" + dateStr + "01")  // 00开头的12位数字
                .build());

        list.add(PackageInfo.builder()
                .implantDevice("植入物器械")
                .packager("张文勇")
                .packageNumber("1416914")
                .sterilizationDate(todayStr)
                .expiryDate(expiryDateStr)
                .packageMaterial("医用纸塑袋")
                .batchNumber("GC" + dateStr + "02")
                .packageBarcode("00" + dateStr + "02")  // 00开头的12位数字
                .build());

        list.add(PackageInfo.builder()
                .implantDevice("植入物器械")
                .packager("王海花")
                .packageNumber("1504484")
                .sterilizationDate(todayStr)
                .expiryDate(expiryDateStr)
                .packageMaterial("医用灭菌袋")
                .batchNumber("GC" + dateStr + "03")
                .packageBarcode("00" + dateStr + "03")  // 00开头的12位数字
                .build());

        list.add(PackageInfo.builder()
                .implantDevice("植入物器械")
                .packager("吴国明")
                .packageNumber("1417232")
                .sterilizationDate(todayStr)
                .expiryDate(expiryDateStr)
                .packageMaterial("医用硬质容器")
                .batchNumber("GC" + dateStr + "04")
                .packageBarcode("00" + dateStr + "04")  // 00开头的12位数字
                .build());

        return list;
    }

    /**
     * 绘制原始码部分（与原方法样式保持一致）
     */
    private void drawOriginalCodeSection(PDDocument document, PDPageContentStream contentStream, PDFont font,
                                   Paperless paperless, float x, float y, float width, float height) throws IOException {
        try {
            // 使用与原方法相同的参数
            float padding = 8;
            float textX = x + padding;
            float topY = y + height - padding; // 内容顶部对齐参考线

            // 二维码参数（与原方法保持一致）
            String defNoPkgCode = paperless.getDefNoPkgCode() != null ? paperless.getDefNoPkgCode() : "-";
            BufferedImage qrCodeImage = zxingUtil.generateQRCodeImage(defNoPkgCode, 100, 100, 1);
            PDImageXObject pdImage = JPEGFactory.createFromImage(document, qrCodeImage);
            float originalWidth = pdImage.getWidth();
            float originalHeight = pdImage.getHeight();
            
            float enlargedWidth = originalWidth * 0.6f;
            float enlargedHeight = originalHeight * 0.6f;
            float imgX = x + width - enlargedWidth - padding;
            float imgY = topY - enlargedHeight;
            contentStream.drawImage(pdImage, imgX, imgY, enlargedWidth, enlargedHeight);

            // 文本内容（与原方法样式一致）
            float labelFontSize = 9.5f;
            float lineGap = 11f;
            float cursorY = topY - labelFontSize; // 首行

            contentStream.beginText();
            contentStream.setFont(font, labelFontSize);
            contentStream.newLineAtOffset(textX, cursorY);
            contentStream.showText("计费码：" + (paperless.getChargingCode() != null ? paperless.getChargingCode() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("定数码：" + (paperless.getDefNoPkgCode() != null ? paperless.getDefNoPkgCode() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("物资编码：" + (paperless.getVarietieCodeNew() != null ? paperless.getVarietieCodeNew() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("产品名称：" + (paperless.getVarietieName() != null ? paperless.getVarietieName() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("厂家：" + (paperless.getManufacturingEntName() != null ? paperless.getManufacturingEntName() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("生产日期：" + (paperless.getBatchProductionDate() != null ? paperless.getBatchProductionDate() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("有效期：" + (paperless.getBatchValidityPeriod() != null ? paperless.getBatchValidityPeriod() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("注册证号：" + (paperless.getApprovalNumber() != null ? paperless.getApprovalNumber() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            String spec = paperless.getSpecificationOrType() != null ? paperless.getSpecificationOrType() : "";
            contentStream.showText("规格型号：" + (spec.length() > 25 ? spec.substring(0, 25) + "..." : spec));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("批号：" + (paperless.getBatch() != null ? paperless.getBatch() : ""));
            //contentStream.newLineAtOffset(0, -lineGap);
            //contentStream.showText("包号：" + "BH001");
            contentStream.endText();

        } catch (WriterException e) {
            throw new IOException("生成二维码失败", e);
        }
    }

    /**
     * 绘制包码部分（与原方法样式保持一致）
     */
    private void drawPackageCodeSection(PDDocument document, PDPageContentStream contentStream, PDFont font,
                                  PackageInfo packageInfo, float x, float y, float width, float height) throws IOException {
        try {
            // 使用与原方法相同的参数
            float padding = 8;
            float textX = x + padding;
            float topY = y + height - padding; // 内容顶部对齐参考线

            // 二维码参数（与原方法保持一致）
            String packageBarcode = packageInfo.getPackageBarcode() != null ? packageInfo.getPackageBarcode() : "-";
            BufferedImage qrCodeImage = zxingUtil.generateQRCodeImage(packageBarcode, 100, 100, 1);
            PDImageXObject pdImage = JPEGFactory.createFromImage(document, qrCodeImage);
            float originalWidth = pdImage.getWidth();
            float originalHeight = pdImage.getHeight();

            float enlargedWidth = originalWidth * 0.6f;
            float enlargedHeight = originalHeight * 0.6f;
            float imgX = x + width - enlargedWidth - padding;
            float imgY = topY - enlargedHeight;
            contentStream.drawImage(pdImage, imgX, imgY, enlargedWidth, enlargedHeight);

            // 文本内容（与原方法样式一致）
            float labelFontSize = 9.5f;
            float lineGap = 11f;
            float cursorY = topY - labelFontSize; // 首行

            contentStream.beginText();
            contentStream.setFont(font, labelFontSize);
            contentStream.newLineAtOffset(textX, cursorY);
            contentStream.showText("包号：" + (packageInfo.getPackageBarcode() != null ? packageInfo.getPackageBarcode() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("包名称：植入物器械");
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("打包人：" + (packageInfo.getPackager() != null ? packageInfo.getPackager() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("灭菌日期：" + (packageInfo.getSterilizationDate() != null ? packageInfo.getSterilizationDate() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("失效日期：" + (packageInfo.getExpiryDate() != null ? packageInfo.getExpiryDate() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("包材质：" + (packageInfo.getPackageMaterial() != null ? packageInfo.getPackageMaterial() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("灭菌机编号：" + (packageInfo.getPackageNumber() != null ? packageInfo.getPackageNumber() : ""));
            contentStream.newLineAtOffset(0, -lineGap);
            contentStream.showText("锅次：" + (packageInfo.getBatchNumber() != null ? packageInfo.getBatchNumber() : ""));
           contentStream.endText();

        } catch (WriterException e) {
            throw new IOException("生成包码二维码失败", e);
        }
    }

    /**
     * 根据PaperlessWithGtDTO生成PDF（包含灭菌包和非灭菌物品）
     */
    public List<com.spd.pojo.vo.PdfGenerationResult> generatePDFsFromGtDTO(com.spd.pojo.dto.PaperlessWithGtDTO dto) throws IOException {
        List<com.spd.pojo.vo.PdfGenerationResult> results = new ArrayList<>();

        for (Map.Entry<String, com.spd.pojo.dto.PaperlessWithGtDTO.OperationRecord> entry : dto.getBody().entrySet()) {
            String documentName = entry.getKey();
            com.spd.pojo.dto.PaperlessWithGtDTO.OperationRecord record = entry.getValue();

            try {
                List<PackageGroup> packageGroups = new ArrayList<>();

                // 处理灭菌包 - 每个包和它的物品作为一组
                if (record.getSterilizationPackages() != null) {
                    for (com.spd.pojo.dto.PaperlessWithGtDTO.SterilizationPackage pkg : record.getSterilizationPackages()) {
                        PackageGroup group = new PackageGroup();
                        group.packageInfo = PackageInfo.builder()
                                .packageBarcode(pkg.getPackageNo())
                                .implantDevice(pkg.getPackageName())
                                .packager(pkg.getPackageUser())
                                .sterilizationDate(pkg.getSteriliseTime())
                                .expiryDate(pkg.getExpireTime())
                                .packageMaterial(pkg.getPackageMaterial())
                                .packageNumber(pkg.getSterNo())
                                .batchNumber(pkg.getSterCycle())
                                .build();

                        if (pkg.getItems() != null) {
                            for (com.spd.pojo.dto.PaperlessWithGtDTO.MedicalItem item : pkg.getItems()) {
                                group.items.add(convertToPaperless(item));
                            }
                        }
                        packageGroups.add(group);
                    }
                }

                // 处理非灭菌物品 - 作为单独一组
                if (record.getItemsWithoutSterilization() != null && !record.getItemsWithoutSterilization().isEmpty()) {
                    PackageGroup group = new PackageGroup();
                    for (com.spd.pojo.dto.PaperlessWithGtDTO.MedicalItem item : record.getItemsWithoutSterilization()) {
                        group.items.add(convertToPaperless(item));
                    }
                    packageGroups.add(group);
                }

                File pdfFile = generatePDFWithPackages(documentName, packageGroups);

                com.spd.pojo.vo.PdfGenerationResult result = new com.spd.pojo.vo.PdfGenerationResult();
                result.setDocumentName(documentName);
                result.setFileAbsolutePath(pdfFile.getAbsolutePath());
                result.setFileRelativePath(paperlessConfig.getPaperlessDir() + "/" + pdfFile.getName());
                result.setSuccess(true);
                result.setDocId(documentName);
                results.add(result);

            } catch (Exception e) {
                com.spd.pojo.vo.PdfGenerationResult result = new com.spd.pojo.vo.PdfGenerationResult();
                result.setDocumentName(documentName);
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                results.add(result);
                log.error("生成PDF失败: {}", documentName, e);
            }
        }

        return results;
    }

    private static class PackageGroup {
        PackageInfo packageInfo;
        List<Paperless> items = new ArrayList<>();
    }

    private Paperless convertToPaperless(com.spd.pojo.dto.PaperlessWithGtDTO.MedicalItem item) {
        Paperless p = new Paperless();
        p.setAdnissTimes(item.getAdnissTimes());
        p.setDefNoPkgCode(item.getDefNoPkgCode());
        p.setChargingCode(item.getChargingCode());
        p.setVarietieCodeNew(item.getVarietieCodeNew());
        p.setVarietieName(item.getVarietieName());
        p.setBatchProductionDate(item.getBatchProductionDate());
        p.setBatchValidityPeriod(item.getBatchValidityPeriod());
        p.setManufacturingEntName(item.getManufacturingEntName());
        p.setApprovalNumber(item.getApprovalNumber());
        p.setSpecificationOrType(item.getSpecificationOrType());
        p.setBatch(item.getBatch());
        p.setHospitalizationNumber(item.getHospitalizationNumber());
        p.setPatientName(item.getPatientName());
        return p;
    }

    private File generatePDFWithPackages(String documentName, List<PackageGroup> packageGroups) throws IOException {
        PDDocument document = new PDDocument();
        InputStream inputStream = classLoader.getResourceAsStream("fonts/NotoSansSC-Regular.ttf");
        PDFont font = PDType0Font.load(document, inputStream);
        PDRectangle pageSize = new PDRectangle(595, 841);

        try {
            int labelsPerPage = 8;
            int labelsPerColumn = 4;
            float labelWidth = pageSize.getWidth() / 2;
            float topMargin = 120;
            float bottomMargin = 60;
            float availableHeight = pageSize.getHeight() - topMargin - bottomMargin;
            float labelHeight = availableHeight / labelsPerColumn;

            Paperless firstItem = packageGroups.isEmpty() || packageGroups.get(0).items.isEmpty() ? null : packageGroups.get(0).items.get(0);

            // 计算总页数
            int totalPages = 0;
            for (PackageGroup group : packageGroups) {
                int groupSize = (group.packageInfo != null ? 1 : 0) + group.items.size();
                totalPages += Math.max(1, (int) Math.ceil((double) groupSize / labelsPerPage));
            }

            int currentPage = 0;

            for (PackageGroup group : packageGroups) {
                List<Object> groupLabels = new ArrayList<>();
                if (group.packageInfo != null) {
                    groupLabels.add(group.packageInfo);
                }
                groupLabels.addAll(group.items);

                int groupPages = Math.max(1, (int) Math.ceil((double) groupLabels.size() / labelsPerPage));
                int printed = 0;

                for (int pageIndex = 0; pageIndex < groupPages; pageIndex++) {
                    currentPage++;
                    PDPage page = new PDPage(pageSize);
                    document.addPage(page);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);

                    // Logo和标题
                    InputStream logoStream = classLoader.getResourceAsStream("logo/peking.png");
                    if (logoStream != null) {
                        byte[] logoBytes = logoStream.readAllBytes();
                        PDImageXObject logoImage = PDImageXObject.createFromByteArray(document, logoBytes, "logo/peking.png");
                        float logoScaledWidth = logoImage.getWidth() * 0.5f;
                        float logoScaledHeight = logoImage.getHeight() * 0.5f;
                        contentStream.drawImage(logoImage, (pageSize.getWidth() - logoScaledWidth) / 2,
                                pageSize.getHeight() - 20 - logoScaledHeight, logoScaledWidth, logoScaledHeight);
                    }

                    contentStream.beginText();
                    contentStream.setFont(font, 18);
                    float titleWidth = font.getStringWidth("病案条码信息页") / 1000 * 18;
                    contentStream.newLineAtOffset((pageSize.getWidth() - titleWidth) / 2, pageSize.getHeight() - 70);
                    contentStream.showText("病案条码信息页");
                    contentStream.endText();

                    // 表头
                    contentStream.beginText();
                    contentStream.setFont(font, 12);
                    contentStream.newLineAtOffset(50, pageSize.getHeight() - topMargin + 10);
                    contentStream.showText("姓名：" + (firstItem != null && firstItem.getPatientName() != null ? firstItem.getPatientName() : ""));
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText("住院号：" + (firstItem != null && firstItem.getHospitalizationNumber() != null ? firstItem.getHospitalizationNumber() : ""));
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText("住院次数：" + (firstItem != null && firstItem.getAdnissTimes() != null ? firstItem.getAdnissTimes() : ""));
                    contentStream.endText();

                    contentStream.setLineWidth(1);
                    contentStream.moveTo(50, pageSize.getHeight() - topMargin);
                    contentStream.lineTo(545, pageSize.getHeight() - topMargin);
                    contentStream.stroke();

                    // 绘制标签
                    int labelsThisPage = Math.min(labelsPerPage, groupLabels.size() - printed);
                    for (int i = 0; i < labelsThisPage; i++) {
                        int column = i % 2;
                        int row = i / 2;
                        Object label = groupLabels.get(printed + i);

                        float cellX = column * labelWidth + 10;
                        float cellY = pageSize.getHeight() - topMargin - (row + 1) * labelHeight + 10;
                        float cellW = labelWidth - 20;
                        float cellH = labelHeight - 20;

                        contentStream.setLineWidth(0.5f);
                        contentStream.setStrokingColor(new Color(200, 200, 200));
                        contentStream.addRect(cellX, cellY, cellW, cellH);
                        contentStream.stroke();
                        contentStream.setStrokingColor(Color.BLACK);

                        if (label instanceof Paperless) {
                            drawOriginalCodeSection(document, contentStream, font, (Paperless) label, cellX, cellY, cellW, cellH);
                        } else if (label instanceof PackageInfo) {
                            drawPackageCodeSection(document, contentStream, font, (PackageInfo) label, cellX, cellY, cellW, cellH);
                        }
                    }
                    printed += labelsThisPage;

                    // 页脚
                    contentStream.setLineWidth(1);
                    contentStream.moveTo(50, bottomMargin);
                    contentStream.lineTo(545, bottomMargin);
                    contentStream.stroke();

                    contentStream.beginText();
                    contentStream.setFont(font, 8);
                    String footerText = currentPage + " / " + totalPages;
                    float footerWidth = font.getStringWidth(footerText) / 1000 * 8;
                    contentStream.newLineAtOffset(pageSize.getWidth() - footerWidth - 50, bottomMargin - 30);
                    contentStream.showText(footerText);
                    contentStream.endText();

                    contentStream.close();
                }
            }

            Path locatePath = ensureDirectory(Paths.get(paperlessConfig.getFileLocation(), paperlessConfig.getPaperlessDir()));
            String filename = documentName + ".pdf";
            Path filePath = locatePath.resolve(filename);
            document.save(filePath.toString());
            return filePath.toFile();

        } finally {
            if (inputStream != null) inputStream.close();
            document.close();
        }
    }
}
