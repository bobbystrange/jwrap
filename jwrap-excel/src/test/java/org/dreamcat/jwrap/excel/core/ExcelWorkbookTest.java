package org.dreamcat.jwrap.excel.core;

import org.dreamcat.jwrap.excel.BaseTest;
import org.dreamcat.jwrap.excel.content.ExcelStringContent;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/21
 */
public class ExcelWorkbookTest implements BaseTest {

    @Test
    public void export() {
        ExcelSheet sheet = new ExcelSheet("sheet1");
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("A1:B2"), 0, 0, 2, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("C1:C2"), 0, 2, 2, 1));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("D1:D2"), 0, 3, 2, 1));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("A3:B3"), 2, 0, 1, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("C3"), 2, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("D3"), 2, 3));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("A4:B4"), 3, 0, 1, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("C4"), 3, 2));
        sheet.getCells().add(new ExcelCell(ExcelStringContent.from("D4"), 3, 3));

        writeXlsx("book_ExcelWorkbookTest_export", sheet);
    }

    @Test
    public void fromAnnotationRowSheetTestTest() {
        readXlsx("book_AnnotationRowSheetTest_test", this::printSheetVerbose);
    }
}
