package org.dreamcat.jwrap.conv.pdf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.function.ThrowableBiConsumer;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.jwrap.conv.core.PdfSplitter;

/**
 * Create by tuke on 2019-04-01
 */
@Slf4j
@RequiredArgsConstructor
public class PdfConverter {

    private final PdfSplitter splitter;
    private final ThrowableBiConsumer<String, String> converter;

    public void splitAndConvertDir(String pdfDir, String outputDir) {
        log.info("start to split and convert");
        long timestamp = System.currentTimeMillis();

        File[] pdfFiles = new File(pdfDir).listFiles();
        Objects.requireNonNull(pdfFiles);
        for (File pdfFile : pdfFiles) {
            try {
                splitAndConvertFile(pdfFile, outputDir);
            } catch (Exception e) {
                log.error("fail to convert {}, {}",
                        pdfFile.getAbsolutePath(), e.getMessage());
            }
        }

        timestamp = System.currentTimeMillis() - timestamp;
        log.info("total cost {}ms", timestamp);
    }

    public void splitAndConvertFile(File pdfFile, String outputDir) throws Exception {
        String pdfPath = pdfFile.getAbsolutePath();
        log.info("convert pdf {}", pdfPath);
        long timestamp = System.currentTimeMillis();

        String prefixName = FileUtil.prefix(pdfPath);
        String outputPath = outputDir + File.separator + prefixName;
        File outputPathDir = new File(outputPath);
        if (!outputPathDir.mkdirs() && !outputPathDir.exists()) {
            log.error("failed to create dir {}", outputPath);
            return;
        }

        log.info("pdf split {}", pdfPath);
        long splitTimestamp = System.currentTimeMillis();
        int partNumber = splitter.splitPdf(pdfPath, outputPath);
        splitTimestamp = System.currentTimeMillis() - splitTimestamp;
        log.info("pdf split cost {}ms", splitTimestamp);

        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= partNumber; i++) {
            list.add(i);
        }
        list.parallelStream().forEach(it -> {
            String splittedPdfPath =
                    outputPath + File.separator + prefixName + "-" + it + ".pdf";
            log.info("pdf2image {}", splittedPdfPath);
            long t = System.currentTimeMillis();
            try {
                converter.accept(splittedPdfPath, outputPath);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            t = System.currentTimeMillis() - t;
            log.info("pdf2image cost {}ms", t);
        });

        timestamp = System.currentTimeMillis() - timestamp;
        log.info("convert cost {}ms", timestamp);
    }
}
