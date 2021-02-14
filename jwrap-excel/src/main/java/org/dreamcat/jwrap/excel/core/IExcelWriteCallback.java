package org.dreamcat.jwrap.excel.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.jwrap.excel.content.IExcelContent;

/**
 * Create by tuke on 2020/7/26
 */
public interface IExcelWriteCallback {

    default void onCreateSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        // nop
    }

    default void onFinishSheet(Workbook workbook, Sheet sheet, int sheetIndex) {
        // nop
    }

    default void onCreateCell(Workbook workbook, Sheet sheet, int sheetIndex, Row row, Cell cell) {
        // nop
    }

    default void onFinishCell(
            Workbook workbook, Sheet sheet, int sheetIndex,
            Row row, Cell cell, IExcelContent content, CellStyle style) {
        // nop
    }
}
