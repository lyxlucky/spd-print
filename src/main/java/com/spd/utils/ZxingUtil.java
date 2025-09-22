package com.spd.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZxingUtil {

    public BufferedImage generateQRCodeImage(String text, int width, int height,int margin) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, margin);
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    public BufferedImage generateCode128(String text,int width,int height,int margin) throws WriterException {
        return generateCode128(text, text, width, height, margin);
    }

    public BufferedImage generateCode128(String barcodeContent, String displayText, int width, int height, int margin) throws WriterException {
        Code128Writer writer = new Code128Writer();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, margin);
        BitMatrix bitMatrix = writer.encode(barcodeContent, BarcodeFormat.CODE_128, width, height - 11, hints); // Reduce height for text
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        // Create a new image with space for text
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combinedImage.createGraphics();
        // Fill with white background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // Draw the barcode
        g.drawImage(barcodeImage, 0, 0, null);
        // Draw the text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(displayText);
        int textX = (width - textWidth) / 2;
        int textY = height - 2; // Adjust vertical position as needed
        g.drawString(displayText, textX, textY);
        g.dispose();
        return combinedImage;
    }

}
