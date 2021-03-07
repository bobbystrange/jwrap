package org.dreamcat.jwrap.conv.jacob;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by tuke on 2019-03-31
 */
@Slf4j
public class JacobWps2015Converter extends JacobConverter {

    private static final String WORD_PROGRAM_ID = "KWPS.Application";
    private static final String PPT_PROGRAM_ID = "KWPP.Application";
    private static final String EXECL_PROGRAM_ID = "KET.Application";
    private static final int wordSaveAsPDF = 17;
    private static final int excelExportAsFixedFormatPDF = 0;
    private static final int pptSaveAsPDF = 32;

    private static final String WPS_PPT_PROCESS_NAME = "wpp.exe";
    private static final String WPS_WORD_PROCESS_NAME = "wpp.exe";
    private static final String WPS_EXCEL_PROCESS_NAME = "wpp.exe";

    public synchronized JacobWps2015Converter withTimeout(int timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    @Override
    public void ppt2Pdf(String inputFile, String outputFile) {
        ActiveXComponent activeXComponent = null;
        ActiveXComponent workbook = null;
        try {
            ComThread.InitSTA();
            activeXComponent = new ActiveXComponent(PPT_PROGRAM_ID);
            workbook = activeXComponent.invokeGetComponent("Presentations")
                    .invokeGetComponent("Open",
                            new Variant(inputFile),
                            new Variant(true) //readonly
                    );
            workbook.invoke("SaveAs", new Variant(outputFile), new Variant(pptSaveAsPDF));
        } finally {
            if (workbook != null) {
                workbook.invoke("Close");
                workbook.safeRelease();
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
                activeXComponent.safeRelease();
            }
            ComThread.Release();
        }
    }

    @Override
    public void word2Pdf(String inputFile, String outputFile) {
        ActiveXComponent activeXComponent = null;
        ActiveXComponent workbook = null;
        try {
            ComThread.InitSTA();
            activeXComponent = new ActiveXComponent(WORD_PROGRAM_ID);
            workbook = activeXComponent.invokeGetComponent("Documents")
                    .invokeGetComponent("Open",
                            new Variant(inputFile),
                            new Variant(true),
                            new Variant(true) //readonly
                    );
            workbook.invoke("SaveAs", new Variant(outputFile), new Variant(wordSaveAsPDF));
        } finally {
            if (workbook != null) {
                workbook.invoke("Close");
                workbook.safeRelease();
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
                activeXComponent.safeRelease();
            }
            ComThread.Release();
        }
    }

    @Override
    public void excel2Pdf(String inputFile, String outputFile) {
        ActiveXComponent activeXComponent = null;
        Dispatch workbooks = null;
        Dispatch workbook = null;
        try {
            ComThread.InitSTA();
            activeXComponent = new ActiveXComponent(EXECL_PROGRAM_ID);
            activeXComponent.setProperty("Visible", new Variant(false));
            workbooks = activeXComponent.getProperty("Workbooks").toDispatch();
            //workbook = Dispatch.call(workbooks, "Open", filename).toDispatch();//这一句也可以的
            workbook = Dispatch.invoke(
                    workbooks,
                    "Open",
                    Dispatch.Method,
                    new Object[]{inputFile, 0, true},
                    new int[1]).toDispatch();
            // Dispatch.invoke(workbook,"SaveAs",Dispatch.Method,new Object[]{pdfFilePath,xlTypePDF},new int[1]);
            Dispatch.call(workbook, "ExportAsFixedFormat", excelExportAsFixedFormatPDF, outputFile);
        } finally {
            if (workbook != null) {
                Dispatch.call(workbook, "Close");
                workbook.safeRelease();
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
                activeXComponent.safeRelease();
            }
            ComThread.Release();
        }
    }

    @Override
    protected String pptProcessName() {
        return WPS_PPT_PROCESS_NAME;
    }

    @Override
    protected String wordProcessName() {
        return WPS_WORD_PROCESS_NAME;
    }

    @Override
    protected String excelProcessName() {
        return WPS_EXCEL_PROCESS_NAME;
    }
}
