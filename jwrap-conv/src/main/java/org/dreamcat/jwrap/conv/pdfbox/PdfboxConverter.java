package org.dreamcat.jwrap.conv.pdfbox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.jwrap.conv.core.Pdf2jpgConverter;
import org.dreamcat.jwrap.conv.core.Pdf2pngConverter;

/**
 * Create by tuke on 2019-03-31
 */
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfboxConverter implements Pdf2jpgConverter, Pdf2pngConverter {

    @Builder.Default
    private int thumbnailWidth = 800;

    @Override
    public void pdf2jpg(String pdfPath, String outputPath) throws Exception {
        File pdfFile = new File(pdfPath);
        String prefixName = FileUtil.prefix(pdfPath);
        String imagePath = outputPath + File.separator + prefixName + ".jpg";
        File imageFile = new File(imagePath);

        try (PDDocument pdf = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);
            BufferedImage image = pdfRenderer.renderImage(0, 2);
            this.toFile(image, imageFile, "jpg");
            image.flush();
        }
    }

    @Override
    public void pdf2png(String pdfPath, String outputPath) throws Exception {
        File pdfFile = new File(pdfPath);
        String prefixName = FileUtil.prefix(pdfPath);
        String imagePath = outputPath + File.separator + prefixName + ".png";
        File imageFile = new File(imagePath);

        try (PDDocument pdf = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);
            BufferedImage image = pdfRenderer.renderImage(0, 2);
            this.toFile(image, imageFile, "png");
            image.flush();
        }
    }

    protected void toFile(BufferedImage image, File imageFile) throws IOException {
        String imageFormat = FileUtil.suffix(imageFile.getName());
        toFile(image, imageFile, imageFormat);
    }

    protected void toFile(BufferedImage image, File imageFile, String imageFormat)
            throws IOException {
        Thumbnails.of(image)
                .width(thumbnailWidth)
                .outputQuality(1)
                .outputFormat(imageFormat)
                .toFile(imageFile);
    }
}
