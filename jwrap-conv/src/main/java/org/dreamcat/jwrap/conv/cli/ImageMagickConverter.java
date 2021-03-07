package org.dreamcat.jwrap.conv.cli;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.common.io.ShellUtil;
import org.dreamcat.jwrap.conv.core.Pdf2imgConverter;

/**
 * Create by tuke on 2019-03-31
 * <p>
 * Deprecated as it is slower and maybe lose some elements in some pdf page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageMagickConverter implements Pdf2imgConverter {

    /**
     * Use a value of around 175 and the text should become clearer than before
     */
    @Builder.Default
    private int density = 175;

    @Override
    public void pdf2jpg(String inputPath, String outputPath) throws Exception {
        pdf2img(inputPath, outputPath, "jpg");
    }

    @Override
    public void pdf2png(String inputPath, String outputPath) throws Exception {
        pdf2img(inputPath, outputPath, "png");
    }

    private void pdf2img(String inputPath, String outputPath, String imageType) throws Exception {
        String prefixName = FileUtil.prefix(inputPath);
        String imagePath = outputPath + File.separator + prefixName + "." + imageType;
        ShellUtil.exec(String.format("convert -density %d %s %s", density, inputPath, imagePath));
    }
}
