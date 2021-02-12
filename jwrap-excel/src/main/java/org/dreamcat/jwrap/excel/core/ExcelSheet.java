package org.dreamcat.jwrap.excel.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/20
 */
@Data
public class ExcelSheet implements IExcelSheet {

    private final String name;
    private final List<IExcelCell> cells;
    private org.dreamcat.jwrap.excel.core.IExcelWriteCallback writeCallback;
    // not load styles in from case
    private boolean noStyle;

    public ExcelSheet(String name) {
        this.name = name;
        this.cells = new ArrayList<>();
    }

    public static ExcelSheet from(Workbook workbook, Sheet sheet) {
        return from(workbook, sheet, true);
    }

    public static ExcelSheet from(Workbook workbook, Sheet sheet, boolean noStyle) {
        ExcelSheet excelSheet = new ExcelSheet(sheet.getSheetName());
        excelSheet.setNoStyle(noStyle);

        List<IExcelCell> cells = excelSheet.getCells();
        int rowNum = sheet.getPhysicalNumberOfRows();
        Map<Integer, Map<Integer, org.dreamcat.jwrap.excel.core.ExcelCell>> cellMap = new TreeMap<>();
        for (int i = 0; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int start = row.getFirstCellNum();
            if (start == -1) continue;
            int end = row.getLastCellNum();

            for (int j = start; j < end; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                IExcelContent content = IExcelContent.from(cell);
                org.dreamcat.jwrap.excel.core.ExcelCell excelCell = null;

                if (noStyle) {
                    excelCell = new org.dreamcat.jwrap.excel.core.ExcelCell(content, i, j);
                    cells.add(excelCell);
                    continue;
                }

                Hyperlink hyperlink = cell.getHyperlink();
                CellStyle style = cell.getCellStyle();

                if (hyperlink != null || style != null) {
                    org.dreamcat.jwrap.excel.core.ExcelRichCell richCell = new ExcelRichCell(content, i, j);
                    if (hyperlink != null) {
                        richCell.setHyperLink(ExcelHyperLink.from(hyperlink));
                    }
                    if (style != null) {
                        richCell.setStyle(ExcelStyle.from(style));
                        richCell.setFont(ExcelFont.from(workbook, style));
                    }

                    excelCell = richCell;
                }

                if (excelCell == null) {
                    excelCell = new org.dreamcat.jwrap.excel.core.ExcelCell(content, i, j);
                }

                cells.add(excelCell);
                cellMap.computeIfAbsent(i, it -> new TreeMap<>())
                        .put(j, excelCell);
            }
        }

        int numMergedRegions = sheet.getNumMergedRegions();
        if (numMergedRegions == 0) return excelSheet;

        for (IExcelCell cell : cells) {
            IExcelCell leftCell = getLeftCell(cell, cellMap);
            if (leftCell != null) {
                leftCell.setColumnSpan(cell.getColumnIndex() - leftCell.getColumnIndex());
            }
            IExcelCell topCell = getTopCell(cell, cellMap);
            if (topCell != null) {
                topCell.setRowSpan(cell.getRowIndex() - topCell.getRowIndex());
            }
        }

        // Note merge region for the last cell
        if (!cells.isEmpty()) {
            IExcelCell lastCell = cells.get(cells.size() - 1);
            CellRangeAddress addresses = sheet.getMergedRegion(numMergedRegions - 1);
            int ri = addresses.getFirstRow();
            int ci = addresses.getFirstColumn();
            if (lastCell.getRowIndex() == ri &&
                    lastCell.getColumnIndex() == ci) {
                lastCell.setRowSpan(addresses.getLastRow() - ri);
                lastCell.setColumnSpan(addresses.getLastColumn() - ci);
            }
        }

        return excelSheet;
    }

    private static IExcelCell getLeftCell(IExcelCell cell,
            Map<Integer, Map<Integer, org.dreamcat.jwrap.excel.core.ExcelCell>> map) {
        int ri = cell.getRowIndex();
        int ci = cell.getColumnIndex();
        org.dreamcat.jwrap.excel.core.ExcelCell excelCell;
        while (--ci >= 0) {
            excelCell = map.getOrDefault(ri, Collections.emptyMap()).get(ci);
            if (excelCell != null) return excelCell;
        }
        return null;
    }

    private static IExcelCell getTopCell(IExcelCell cell,
            Map<Integer, Map<Integer, org.dreamcat.jwrap.excel.core.ExcelCell>> map) {
        int ri = cell.getRowIndex();
        int ci = cell.getColumnIndex();
        ExcelCell excelCell;
        while (--ri >= 0) {
            excelCell = map.getOrDefault(ri, Collections.emptyMap()).get(ci);
            if (excelCell != null) return excelCell;
        }
        return null;
    }

    @Override
    public IExcelWriteCallback writeCallback() {
        return writeCallback;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return cells.iterator();
    }
}
