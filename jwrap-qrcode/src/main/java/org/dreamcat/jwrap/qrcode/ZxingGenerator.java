package org.dreamcat.jwrap.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.dreamcat.common.io.image.ImageUtil;

/**
 * Create by tuke on 2020/5/27
 */
public class ZxingGenerator {

    Map<EncodeHintType, Object> hints;
    private int width;
    private int height;
    private Supplier<Writer> writerConstructor;

    public static Builder builder() {
        return new Builder(new ZxingGenerator());
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
        Writer writer = writerConstructor.get();
        BitMatrix bitMatrix;
        try {
            bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static class Builder {

        private final ZxingGenerator target;

        public Builder(ZxingGenerator target) {
            this.target = target;
            this.target.hints = new EnumMap<>(EncodeHintType.class);
        }

        public Builder shape(int width, int height) {
            target.width = width;
            target.height = height;
            return this;
        }

        public Builder usingMultiFormatWriter() {
            target.writerConstructor = MultiFormatWriter::new;
            return this;
        }

        public Builder charset(String charset) {
            target.hints.put(EncodeHintType.CHARACTER_SET, charset);
            return this;
        }

        // L 7%, M 15%, Q 25%, H 30%
        public Builder errorCorrectionLevel(ErrorCorrectionLevel level) {
            target.hints.put(EncodeHintType.ERROR_CORRECTION, level);
            return this;
        }

        // 1, 2, 3, 4, default is 4
        public Builder margin(int margin) {
            target.hints.put(EncodeHintType.MARGIN, margin);
            return this;
        }

        public ZxingGenerator build() {
            if (target.writerConstructor == null) {
                usingMultiFormatWriter();
            }
            return target;
        }
    }
}
