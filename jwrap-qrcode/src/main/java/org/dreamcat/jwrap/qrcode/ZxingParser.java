package org.dreamcat.jwrap.qrcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Create by tuke on 2020/5/27
 */
public class ZxingParser {

    private Map<DecodeHintType, Object> hints;
    private Supplier<Reader> readerConstructor;

    public static Builder builder() {
        return new Builder(new ZxingParser());
    }

    public String parse(BufferedImage image) {
        Reader reader = readerConstructor.get();
        BinaryBitmap binaryBitmap = new BinaryBitmap(
                new HybridBinarizer(new BufferedImageLuminanceSource(image)));

        try {
            Result result = reader.decode(binaryBitmap, hints);
            return result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {

        private final ZxingParser target;

        public Builder(ZxingParser target) {
            this.target = target;
            this.target.hints = new EnumMap<>(DecodeHintType.class);
        }

        public Builder usingMultiFormat() {
            target.readerConstructor = MultiFormatReader::new;
            return this;
        }

        public Builder charset(String charset) {
            target.hints.put(DecodeHintType.CHARACTER_SET, charset);
            return this;
        }

        public ZxingParser build() {
            if (target.readerConstructor == null) {
                usingMultiFormat();
            }
            return target;
        }
    }

}
