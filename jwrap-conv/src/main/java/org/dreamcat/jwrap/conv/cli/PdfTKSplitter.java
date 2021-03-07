package org.dreamcat.jwrap.conv.cli;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.common.io.ShellUtil;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.jwrap.conv.core.PdfSplitter;

/**
 * Create by tuke on 2019-03-31
 */
@Slf4j
public class PdfTKSplitter implements PdfSplitter {

    private static final Pattern PATTERN = Pattern.compile("pg_0*?([1-9]?[0-9]*).pdf");

    @Override
    public int splitPdf(String pdfPath, String outputPath, int split) throws Exception {
        File pdfFile = new File(pdfPath);
        String prefixName = FileUtil.prefix(pdfPath);
        String baseName = pdfFile.getName();

        String[] cmd1 = new String[]{
                "/bin/bash",
                "-c",
                String.format("/bin/cp %s %s/", pdfPath, outputPath)
        };
        String[] cmd2 = new String[]{
                "/bin/bash",
                "-c",
                String.format("cd %s && /usr/bin/pdftk %s burst && rm %s",
                        outputPath, baseName, baseName)
        };
        log.info("invoking ({}):\t{}", cmd1, ShellUtil.exec(cmd1));
        // pdftk input.pdf output output.pdf owner_pw
        log.info("invoking ({}):\t{}", cmd2, ShellUtil.exec(cmd2));

        File[] pdfFiles = new File(outputPath).listFiles();
        if (ObjectUtil.isEmpty(pdfFiles)) {
            throw new IllegalStateException("'pdftk' expects PDF files in " + outputPath);
        }

        for (File page : pdfFiles) {
            String name = page.getName();
            Matcher matcher = PATTERN.matcher(name);
            if (!matcher.matches()) {
                // delete doc_data.txt
                if (!page.delete() && page.exists()) {
                    log.warn("failed to delete {}", page);
                }
                continue;
            }

            int seq = Integer.parseInt(matcher.group(1));
            File outputFile = new File(outputPath, prefixName + "-" + seq + ".pdf");
            if (!page.renameTo(outputFile)) {
                log.warn("failed to rename {} to {}", page, outputFile);
            }
        }
        // exclude doc_data.txt
        return pdfFiles.length - 1;
    }
}
