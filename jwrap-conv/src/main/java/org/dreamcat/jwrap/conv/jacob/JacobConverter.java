package org.dreamcat.jwrap.conv.jacob;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.io.CmdUtil;
import org.dreamcat.common.io.FileUtil;
import org.dreamcat.common.util.ThreadUtil;


/**
 * Create by tuke on 2019-03-31
 */
@Slf4j
public abstract class JacobConverter {

    protected int timeout = 15;

    protected TimeUnit unit = TimeUnit.SECONDS;

    abstract void ppt2Pdf(String inputFile, String outputFile);

    abstract void word2Pdf(String inputFile, String outputFile);

    abstract void excel2Pdf(String inputFile, String outputFile);

    protected abstract String pptProcessName();

    protected abstract String wordProcessName();

    protected abstract String excelProcessName();

    public final void office2Pdf(String inputFile, String pdfFile) {
        log.info("starting office2pdf process on wps, convert {} to {}", inputFile, pdfFile);
        long timestamp = System.currentTimeMillis();

        String suffix = FileUtil.suffix(inputFile);
        log.info("get suffix {} from {}", suffix, inputFile);

        FutureTask<Exception> task;
        String processName;
        switch (suffix) {
            case "ppt":
            case "pptx":
                task = ThreadUtil.futureTask(() -> this.ppt2Pdf(inputFile, pdfFile));
                processName = pptProcessName();
                break;
            case "doc":
            case "docx":
            case "txt":
                task = ThreadUtil.futureTask(() -> this.word2Pdf(inputFile, pdfFile));
                processName = wordProcessName();
                break;
            case "xls":
            case "xlsx":
                task = ThreadUtil.futureTask(() -> this.excel2Pdf(inputFile, pdfFile));
                processName = excelProcessName();
                break;
            default:
                throw new IllegalArgumentException("File format does not support conversion");
        }
        ThreadUtil.getExceptionFutureTask(task, timeout, unit, e -> {
            log.error(e.getMessage());
            CmdUtil.killProcess(processName);
        });

        timestamp = System.currentTimeMillis() - timestamp;
        log.info("end office2pdf process on wps, convert {} to {}, cost {}ms",
                inputFile, pdfFile, timestamp);
    }

}
