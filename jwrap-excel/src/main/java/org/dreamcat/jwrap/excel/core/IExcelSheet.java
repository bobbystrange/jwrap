package org.dreamcat.jwrap.excel.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelSheet extends Iterable<org.dreamcat.jwrap.excel.core.IExcelCell> {

    String getName();

    default org.dreamcat.jwrap.excel.core.IExcelWriteCallback writeCallback() {
        return null;
    }

    default void fill(Workbook workbook, Sheet sheet, int sheetIndex, CellStyle defaultStyle,
            Font defaultFont) {
        IExcelWriteCallback writeCallback = writeCallback();
        if (writeCallback != null) writeCallback.onCreateSheet(workbook, sheet, sheetIndex);
        for (IExcelCell excelCell : this) {
            int ri = excelCell.getRowIndex();
            int ci = excelCell.getColumnIndex();
            IExcelContent cellContent = excelCell.getContent();
            ExcelFont cellFont = excelCell.getFont();
            ExcelStyle cellStyle = excelCell.getStyle();
            ExcelHyperLink cellLink = excelCell.getHyperLink();

            if (excelCell.hasMergedRegion()) {
                int rs = excelCell.getRowSpan();
                int cs = excelCell.getColumnSpan();
                if (rs > 1 || cs > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            ri, ri + rs - 1, ci, ci + cs - 1));
                }
            }

            Row row = sheet.getRow(ri);
            if (row == null) {
                row = sheet.createRow(ri);
            }
            Cell cell = row.createCell(ci);
            if (writeCallback != null)
                writeCallback.onCreateCell(workbook, sheet, sheetIndex, row, cell);

            cellContent.fill(cell);
            if (cellLink != null) {
                cellLink.fill(excelCell, workbook, cell);
            }

            Font font = null;
            CellStyle style = null;
            if (cellStyle == null && cellFont == null) {
                cell.setCellStyle(defaultStyle);
            } else {
                style = workbook.createCellStyle();

                // cellStyle == null && cellFont != null
                if (cellStyle == null) {
                    font = workbook.createFont();
                    cellFont.fill(font);
                    style.setFont(font);
                }
                // cellStyle != null
                else if (cellFont == null) {
                    cellStyle.fill(style, defaultFont);
                }
                // cellStyle != null && cellFont != null
                else {
                    font = workbook.createFont();
                    cellFont.fill(font);
                    cellStyle.fill(style, font);
                }

                cell.setCellStyle(style);
            }
            if (writeCallback != null)
                writeCallback
                        .onFinishCell(workbook, sheet, sheetIndex, row, cell, cellContent, style,
                                font);
        }
        if (writeCallback != null) writeCallback.onFinishSheet(workbook, sheet, sheetIndex);
    }
}
