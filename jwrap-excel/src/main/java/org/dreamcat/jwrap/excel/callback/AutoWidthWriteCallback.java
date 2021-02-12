package org.dreamcat.jwrap.excel.callback;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.jwrap.excel.core.IExcelWriteCallback;

/**
 * Create by tuke on 2020/8/13
 */
public class AutoWidthWriteCallback implements IExcelWriteCallback {

    private int lastColumnNum;

    @Override
    public void onCreateCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell) {
        int index = cell.getColumnIndex();
        if (index > lastColumnNum) lastColumnNum = index;
    }

    @Override
    public void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        for (int i = 0; i <= lastColumnNum; i++) {
            sheet.autoSizeColumn(i, true);
        }
    }
}
