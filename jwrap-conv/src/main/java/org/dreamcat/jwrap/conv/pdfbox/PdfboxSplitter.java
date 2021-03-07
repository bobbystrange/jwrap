package org.dreamcat.jwrap.conv.pdfbox;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.jwrap.conv.core.PdfSplitter;

/**
 * Create by tuke on 2019-03-31
 */
@Slf4j
public class PdfboxSplitter implements PdfSplitter {

    @Override
    public int splitPdf(String inputPath, String outputPath, int split) throws IOException {
        File pdfFile = new File(inputPath);
        String prefixName = FileUtil.prefix(inputPath);

        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(pdfFile);
            if (pdf.isEncrypted()) {
                pdf = PDDocument.load(pdfFile, "");
                pdf.setAllSecurityToBeRemoved(true);
            }

            Splitter splitter = new Splitter();
            splitter.setSplitAtPage(split);
            List<PDDocument> pages = splitter.split(pdf);
            Iterator<PDDocument> iterator = pages.listIterator();

            int partNumber = 0;
            while (iterator.hasNext()) {
                partNumber++;
                String splitterFilePath = outputPath + File.separator + prefixName +
                        "-" + partNumber + ".pdf";
                try (PDDocument pd = iterator.next()) {
                    pd.save(splitterFilePath);
                }
            }

            return partNumber;
        } finally {
            // on debug mode, it will print 'ScratchFileBuffer not closed!'
            if (pdf != null) {
                pdf.close();
            }
        }
    }

}
