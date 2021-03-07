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
public class JacobWpsConverter extends JacobConverter {

    private static final int ppSaveAsPDF = 32;
    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;

    private static final String WPS_PPT_PROCESS_NAME = "wpp.exe";
    private static final String WPS_WORD_PROCESS_NAME = "wpp.exe";
    private static final String WPS_EXCEL_PROCESS_NAME = "wpp.exe";

    public synchronized JacobWpsConverter withTimeout(int timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    @Override
    public void ppt2Pdf(String inputFile, String pdfFile) {
        ComThread.InitSTA(true);
        ActiveXComponent activeXComponent = new ActiveXComponent("KWPP.Application");

        Dispatch ppts = activeXComponent.getProperty("Presentations").toDispatch();
        log.info("invoking open Presentations {}", inputFile);
        Dispatch ppt = Dispatch.call(ppts, "Open", inputFile, true, false).toDispatch();
        Dispatch.invoke(ppt, "SaveAs", Dispatch.Method, new Object[]{
                pdfFile, new Variant(ppSaveAsPDF)}, new int[1]);

        Dispatch.call(ppt, "Close");
        activeXComponent.invoke("Quit");
    }

    @Override
    public void word2Pdf(String inputFile, String pdfFile) {
        ActiveXComponent activeXComponent = new ActiveXComponent("KWPS.Application");
        activeXComponent.setProperty("AutomationSecurity", new Variant(3));
        // get all opened documents
        Dispatch docs = activeXComponent.getProperty("Documents").toDispatch();
        // open specified document
        log.info("invoking open Documents {}", inputFile);
        Dispatch doc = Dispatch.call(docs, "Open", inputFile, false, true).toDispatch();

        // Dispatch.call(doc, "SaveAs", pdfFile, wdFormatPDF
        Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF);

        Dispatch.call(doc, "Close", false);
        activeXComponent.invoke("Quit", 0);
    }

    @Override
    public void excel2Pdf(String inputFile, String pdfFile) {
        ComThread.InitSTA(true);
        ActiveXComponent activeXComponent = new ActiveXComponent("KET.Application");

        activeXComponent.setProperty("Visible", false);
        // disable marco
        activeXComponent.setProperty("AutomationSecurity", new Variant(3));
        Dispatch excels = activeXComponent.getProperty("Workbooks").toDispatch();
        log.info("invoking open Workbooks {}", inputFile);
        Dispatch excel = Dispatch
                .invoke(excels, "Open", Dispatch.Method,
                        new Object[]{inputFile, new Variant(false), new Variant(false)}, new int[9])
                .toDispatch();
        // 0, standard format, 1 mini format
        Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, new Object[]{new Variant(0),
                pdfFile, new Variant(xlTypePDF)
        }, new int[1]);

        Dispatch.call(excel, "Close", new Variant(false));
        activeXComponent.invoke("Quit", new Variant[]{});
        ComThread.Release();
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
