package org.dreamcat.jwrap.excel.callback;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.IExcelWriteCallback;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/27
 */
public class LoggingWriteCallback implements IExcelWriteCallback {

    @Override
    public void onCreateSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        System.out.printf("onCreateSheet:\t %s\n\n", sheet.getSheetName());
    }

    @Override
    public void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        System.out.printf("onFinishSheet:\t %s\n\n", sheet.getSheetName());
    }

    @Override
    public void onCreateCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell) {
        System.out.printf("onCreateCell:\t %s \t %s\n\n", sheet.getSheetName(),
                IExcelContent.from(cell));
    }

    @Override
    public void onFinishCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell,
            IExcelContent content, CellStyle style, Font font) {
        System.out.printf("onFinishCell:\t %s \t %s\n%s\n%s\n\n", sheet.getSheetName(),
                IExcelContent.from(cell),
                ExcelFont.from(workbook, style),
                ExcelStyle.from(cell.getCellStyle()));
    }
}
