package org.dreamcat.jwrap.excel.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dreamcat.common.core.Pair;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.style.ExcelComment;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelSheet extends Iterable<IExcelCell> {

    String getName();

    default IExcelWriteCallback writeCallback() {
        return null;
    }

    default void fill(Sheet sheet, int sheetIndex, IExcelWorkbook<?> excelWorkbook) {
        Workbook workbook = sheet.getWorkbook();
        IExcelWriteCallback writeCallback = writeCallback();
        if (writeCallback != null) {
            writeCallback.onCreateSheet(workbook, sheet, sheetIndex);
        }
        for (IExcelCell excelCell : this) {
            Pair<Row, Cell> rowCell = Private.makeRowCell(excelCell, sheet);
            Row row = rowCell.first();
            Cell cell = rowCell.second();

            if (writeCallback != null) {
                writeCallback.onCreateCell(workbook, sheet, sheetIndex, row, cell);
            }

            // content
            IExcelContent cellContent = excelCell.getContent();
            cellContent.fill(cell);

            // font and style
            CellStyle style = excelWorkbook.makeCellStyle(excelCell, workbook);
            if (style != null) cell.setCellStyle(style);

            // hyperlink
            ExcelHyperLink cellLink = excelCell.getHyperLink();
            if (cellLink != null) {
                cellLink.fill(cell, workbook, excelCell);
            }

            // comment
            ExcelComment excelComment = excelCell.getComment();
            if (excelComment != null) {
                excelComment.fill(cell, sheet);
            }

            if (writeCallback != null)
                writeCallback.onFinishCell(
                        workbook, sheet, sheetIndex,
                        row, cell, cellContent, style);
        }
        if (writeCallback != null) {
            writeCallback.onFinishSheet(workbook, sheet, sheetIndex);
        }
    }

    final class Private {

        private Private() {
        }

        private static Pair<Row, Cell> makeRowCell(
                IExcelCell excelCell, Sheet sheet) {
            int ri = excelCell.getRowIndex();
            int ci = excelCell.getColumnIndex();

            if (excelCell.hasMergedRegion()) {
                int rs = excelCell.getRowSpan();
                int cs = excelCell.getColumnSpan();
                sheet.addMergedRegion(new CellRangeAddress(
                        ri, ri + rs - 1, ci, ci + cs - 1));
            }

            Row row = sheet.getRow(ri);
            if (row == null) {
                row = sheet.createRow(ri);
            }
            Cell cell = row.createCell(ci);
            return Pair.of(row, cell);
        }
    }
}
