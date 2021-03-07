package org.dreamcat.jwrap.conv.cli;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.common.io.ShellUtil;
import org.dreamcat.jwrap.conv.core.Pdf2imgConverter;

/**
 * Create by tuke on 2019-04-04
 */
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MuToolConverter implements Pdf2imgConverter {

    /**
     * resolution in dpi (default: 72)
     */
    @Builder.Default
    private int resolution = 72;

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
        ShellUtil.exec(String
                .format("mutool draw -r %d -o %s %s", resolution, imagePath, inputPath));
    }
}
