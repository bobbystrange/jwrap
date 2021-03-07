package org.dreamcat.jwrap.conv.core;

/**
 * Create by tuke on 2019-04-04
 */
public interface PdfSplitter {

    default int splitPdf(String pdfPath, String outputPath) throws Exception {
        return splitPdf(pdfPath, outputPath, 1);
    }

    /**
     * output pdf files start withs 1
     *
     * @param pdfPath    pdf file path
     * @param outputPath output diretory, make sure the diretory only for one pdf in same time
     * @param split      The number of pages each split document should contain.
     * @return page number
     * @throws Exception any failure
     */
    int splitPdf(String pdfPath, String outputPath, int split) throws Exception;
}
