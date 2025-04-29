package tn.esprit.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import java.awt.image.BufferedImage;

public class QRCodeGenerator {

    public static Image generateQRCodeImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // Convertir BitMatrix en BufferedImage
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF; // noir ou blanc
                bufferedImage.setRGB(x, y, color);
            }
        }

        // Convertir BufferedImage en Image JavaFX
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = bufferedImage.getRGB(x, y);
                pixelWriter.setArgb(x, y, argb);
            }
        }

        return writableImage;
    }
}
