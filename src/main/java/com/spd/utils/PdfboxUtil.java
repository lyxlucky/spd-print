package com.spd.utils;

import com.google.zxing.WriterException;
import com.spd.config.PrintConfig;
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

import java.time.Instant;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class PdfboxUtil {

    @Autowired
    private ZxingUtil zxingUtil;
    @Autowired
    private PrintConfig printConfig;

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
}
