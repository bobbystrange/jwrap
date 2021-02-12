package org.dreamcat.jwrap.excel.core;

import java.io.File;
import java.util.List;
import org.dreamcat.jwrap.excel.content.ExcelStringContent;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/21
 */
public class ExcelWorkbookTest {

    public static void printCells(IExcelSheet sheet) {
        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent());
        }
    }

    public static void printVerbose(IExcelSheet sheet) {
        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    cell.getFont(), cell.getStyle()
            );
        }
    }

    @Test
    public void exportExcelWorkbook() throws Exception {
        ExcelSheet sheet = new ExcelSheet("sheet1");
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("A1:B2"), 0, 0, 2, 2));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("C1:C2"), 0, 2, 2, 1));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("D1:D2"), 0, 3, 2, 1));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("A3:B3"), 2, 0, 1, 2));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("C3"), 2, 2));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("D3"), 2, 3));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("A4:B4"), 3, 0, 1, 2));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("C4"), 3, 2));
        sheet.getCells().add(new ExcelCell(new ExcelStringContent("D4"), 3, 3));

        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<ExcelSheet>();
        book.getSheets().add(sheet);
        book.writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Test
    public void fromExcelWorkbook() throws Exception {
        ExcelWorkbook<ExcelSheet> book = ExcelWorkbook
                .from(new File("/Users/tuke/Downloads/book.xlsx"));

        List<ExcelSheet> sheets = book.getSheets();
        for (ExcelSheet sheet : sheets) {
            printCells(sheet);
        }

        book.writeTo("/Users/tuke/Downloads/book2.xlsx");
    }
}
