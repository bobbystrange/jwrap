package org.dreamcat.jwrap.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreamcat.common.io.image.ImageUtil;

/**
 * Create by tuke on 2020/5/27
 */
public class ZxingQRCode {

    @Getter
    private final Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    private int width;
    private int height;
    private boolean deleteWhiteBorder;
    private BufferedImage logo;
    private float logoRatio = 0.2f;

    private ZxingQRCode() {
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    }

    public static Builder builder() {
        return new Builder(new ZxingQRCode());
    }

    private static BufferedImage deleteWhiteBorder(BitMatrix bitMatrix) {
        int[] rec = bitMatrix.getEnclosingRectangle();
        int l = rec[0];
        int t = rec[1];
        int w = rec[2];
        int h = rec[3];

        BitMatrix matrix = new BitMatrix(w, h);
        matrix.clear();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (bitMatrix.get(i + l, j + t))
                    matrix.set(i, j);
            }
        }

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (matrix.get(x, y)) {
                    image.setRGB(x, y, MatrixToImageConfig.BLACK);
                } else {
                    image.setRGB(x, y, MatrixToImageConfig.WHITE);
                }
            }
        }
        return image;
    }

    public String base64Jpeg(String text) {
        return base64Image(text, "jpeg");
    }

    public String base64Png(String text) {
        return base64Image(text, "png");
    }

    public String base64Image(String text, String imageType) {
        BufferedImage image = generateImage(text);
        return ImageUtil.base64Image(image, imageType);
    }

    public BufferedImage generateImage(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {

            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        BufferedImage image;
        if (deleteWhiteBorder) {
            image = deleteWhiteBorder(bitMatrix);
        } else {
            image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        }
        if (logo != null) {
            return drawLogo(image);
        } else {
            return image;
        }
    }

    private BufferedImage drawLogo(BufferedImage blackWhiteImage) {
        int w = blackWhiteImage.getWidth();
        int h = blackWhiteImage.getHeight();

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(blackWhiteImage, 0, 0, w, h, null);

        int logoWidth = Math.min(logo.getWidth(), (int) (w * logoRatio));
        int logoHeight = Math.min(logo.getHeight(), (int) (h * logoRatio));

        int x = (w - logoWidth) / 2;
        int y = (h - logoHeight) / 2;
        // fill with white
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, logoWidth, logoHeight, w / 20, h / 20);

        g.drawImage(logo, x, y, logoWidth, logoHeight, null);

        // border
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke((w + h) / (float) 100));
        g.drawRect(x, y, logoWidth, logoHeight);
        g.dispose();
        image.flush();
        return image;
    }

    @RequiredArgsConstructor
    public static class Builder {

        private final ZxingQRCode target;

        public Builder shape(int width, int height) {
            target.width = width;
            target.height = height;
            return this;
        }

        public Builder deleteWhiteBorder() {
            return deleteWhiteBorder(true);
        }

        public Builder deleteWhiteBorder(boolean deleteWhiteBorder) {
            target.deleteWhiteBorder = deleteWhiteBorder;
            return this;
        }

        public Builder logo(BufferedImage logo) {
            target.logo = logo;
            return this;
        }

        public Builder logoRatio(float logoRatio) {
            target.logoRatio = logoRatio;
            return this;
        }

        public ZxingQRCode build() {
            return target;
        }
    }
}
