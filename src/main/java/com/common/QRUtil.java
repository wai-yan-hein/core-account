package com.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QRUtil {

    public static ImageIcon createdQRImage(String text) {
        int width = 300; // Width of the QR code
        int height = 300; // Height of the QR code

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF;
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return new ImageIcon(imageBytes);
        } catch (WriterException | IOException e) {
            log.error("createdQRImage : " + e.getMessage());
        }
        return null;
    }
}
