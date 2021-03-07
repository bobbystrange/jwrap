package org.dreamcat.jwrap.conv.jacob;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import java.util.concurrent.TimeUnit;

/**
 * Create by tuke on 2019-03-31
 */
public class JacobMSOffice2007Converter extends JacobConverter {

    private static final int ppFormatPDF = 32;
    private static final int wordFormatPDF = 17;
    private static final int xlsFormatPDF = 0;

    private static final String MSOFFICE_PPT_PROCESS_NAME = "wpp.exe";
    private static final String MSOFFICE_WORD_PROCESS_NAME = "WINWORD.exe";
    private static final String MSOFFICE_EXCEL_PROCESS_NAME = "wpp.exe";

    public synchronized JacobMSOffice2007Converter withTimeout(int timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    @Override
    public void ppt2Pdf(String inputFile, String pdfFile) {
        ActiveXComponent activeXComponent = null;
        Dispatch dispatch = null;
        try {
            ComThread.InitSTA(true);
            activeXComponent = new ActiveXComponent("PowerPoint.Application");
            Dispatch dispatchs = activeXComponent.getProperty("Presentations").toDispatch();
            dispatch = Dispatch.call(dispatchs, "Open", inputFile,
                    true, // readOnly
                    true,// untitled
                    false// withWindow
            ).toDispatch();
            Dispatch.call(dispatch, "SaveAs", pdfFile, ppFormatPDF);
        } finally {
            if (dispatch != null) {
                Dispatch.call(dispatch, "Close", false);
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
            }
            ComThread.Release();
            ComThread.quitMainSTA();
        }
    }

    @Override
    public void word2Pdf(String inputFile, String pdfFile) {
        ActiveXComponent activeXComponent = null;
        Dispatch dispatch = null;
        try {
            ComThread.InitSTA(true);
            activeXComponent = new ActiveXComponent("Word.Application");
            activeXComponent.setProperty("Visible", new Variant(false));
            activeXComponent.setProperty("AutomationSecurity", new Variant(3)); // disable macros

            Dispatch dispatchs = activeXComponent.getProperty("Documents").toDispatch();
            dispatch = Dispatch.call(dispatchs, "Open", inputFile, false, true).toDispatch();
            Dispatch.call(dispatch, "SaveAs", pdfFile, wordFormatPDF);
        } finally {
            if (dispatch != null) {
                Dispatch.call(dispatch, "Close", false);
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
            }
            ComThread.Release();
            ComThread.quitMainSTA();
        }
    }

    @Override
    public void excel2Pdf(String inputFile, String pdfFile) {
        ActiveXComponent activeXComponent = null;
        Dispatch dispatch = null;
        try {
            ComThread.InitSTA(true);
            activeXComponent = new ActiveXComponent("Excel.Application");
            activeXComponent.setProperty("Visible", false);
            Dispatch dispatchs = activeXComponent.getProperty("Workbooks").toDispatch();
            dispatch = Dispatch.call(dispatchs, "Open",
                    inputFile,
                    false,
                    true
            ).toDispatch();
            Dispatch.call(dispatch, "ExportAsFixedFormat", xlsFormatPDF, pdfFile);
        } finally {
            if (dispatch != null) {
                Dispatch.call(dispatch, "Close", false);
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
            }
            ComThread.Release();
            ComThread.quitMainSTA();
        }
    }

    @Override
    protected String pptProcessName() {
        return MSOFFICE_PPT_PROCESS_NAME;
    }

    @Override
    protected String wordProcessName() {
        return MSOFFICE_WORD_PROCESS_NAME;
    }

    @Override
    protected String excelProcessName() {
        return MSOFFICE_EXCEL_PROCESS_NAME;
    }
}
