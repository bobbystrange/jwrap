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
public class JacobMSOffice2010Converter extends JacobConverter {

    private static final int ppSaveAsPDF = 32;
    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;

    private static final String MSOFFICE_PPT_PROCESS_NAME = "wpp.exe";
    private static final String MSOFFICE_WORD_PROCESS_NAME = "WINWORD.exe";
    private static final String MSOFFICE_EXCEL_PROCESS_NAME = "wpp.exe";

    public synchronized JacobMSOffice2010Converter withTimeout(int timeout, TimeUnit unit) {
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
            Dispatch.call(dispatch, "SaveAs", pdfFile, ppSaveAsPDF);
        } finally {
            if (dispatch != null) {
                Dispatch.call(dispatch, "Close");
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
            }
            ComThread.Release();
        }
    }

    @Override
    public void word2Pdf(String inputFile, String pdfFile) {
        ActiveXComponent activeXComponent = null;
        Dispatch dispatch = null;
        try {
            ComThread.InitSTA(true);
            activeXComponent = new ActiveXComponent("Word.Application");
            activeXComponent.setProperty("Visible", false);
            Dispatch dispatchs = activeXComponent.getProperty("Documents").toDispatch();
            dispatch = Dispatch.call(dispatchs, "Open", Dispatch.Method,
                    new Object[]{
                            inputFile,
                            false, //
                            true,// readOnly
                            false,// withWindow
                            new Variant("pwd"),
                    },
                    new int[1]).toDispatch();
            Dispatch.call(dispatch, "SaveAs", pdfFile, ppSaveAsPDF);
            // Dispatch.put(dispatch, "Compatibility", false);
            Dispatch.put(dispatch, "RemovePersonalInformation", false);
            Dispatch.call(dispatch, "ExportAsFixedFormat", pdfFile, wdFormatPDF);
        } finally {
            if (dispatch != null) {
                Dispatch.call(dispatch, "Close");
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
            }
            ComThread.Release();
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
            Dispatch.call(dispatch, "ExportAsFixedFormat", xlTypePDF, pdfFile);
        } finally {
            if (dispatch != null) {
                Dispatch.call(dispatch, "Close");
            }
            if (activeXComponent != null) {
                activeXComponent.invoke("Quit");
            }
            ComThread.Release();
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
