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
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.style.ExcelComment;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/20
 */
@Data
public class ExcelSheet implements IExcelSheet {

    private final String name;
    private final List<IExcelCell> cells;
    private IExcelWriteCallback writeCallback;

    public ExcelSheet(String name) {
        this.name = name;
        this.cells = new ArrayList<>();
    }

    public ExcelSheet(String name, List<IExcelCell> cells) {
        this(name);
        this.cells.addAll(cells);
    }

    public static ExcelSheet from(Sheet sheet, ExcelWorkbook<?> excelWorkbook) {
        ExcelSheet excelSheet = new ExcelSheet(sheet.getSheetName());

        int rowNum = sheet.getPhysicalNumberOfRows();
        Map<Integer, Map<Integer, ExcelCell>> cellMap = new TreeMap<>();
        for (int i = 0; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int start = row.getFirstCellNum();
            if (start == -1) continue;
            int end = row.getLastCellNum();

            for (int j = start; j < end; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                excelSheet.fillCellMap(cellMap, cell, i, j, excelWorkbook);
            }
        }

        excelSheet.computeSpans(cellMap, sheet);
        return excelSheet;
    }

    private void fillCellMap(
            Map<Integer, Map<Integer, ExcelCell>> cellMap,
            Cell cell, int i, int j, ExcelWorkbook<?> excelWorkbook) {
        IExcelContent content = IExcelContent.from(cell);
        ExcelCell excelCell = new ExcelCell(content, i, j);

        CellStyle style = cell.getCellStyle();
        Hyperlink hyperlink = cell.getHyperlink();
        Comment comment = cell.getCellComment();
        if (style != null) {
            ExcelStyle excelStyle = excelWorkbook.reversedStyles.get(style);
            if (excelStyle == null) throw new IllegalStateException("assertion failure");
            excelCell.setStyle(excelStyle);
        }
        if (hyperlink != null) {
            excelCell.setHyperLink(ExcelHyperLink.from(hyperlink));
        }
        if (comment != null) {
            excelCell.setComment(ExcelComment.from(comment));
        }

        cells.add(excelCell);
        cellMap.computeIfAbsent(i, it -> new TreeMap<>())
                .put(j, excelCell);
    }

    private void computeSpans(
            Map<Integer, Map<Integer, ExcelCell>> cellMap,
            Sheet sheet) {
        int numMergedRegions = sheet.getNumMergedRegions();
        if (numMergedRegions == 0) return;

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
    }

    private static IExcelCell getLeftCell(IExcelCell cell,
            Map<Integer, Map<Integer, ExcelCell>> map) {
        int ri = cell.getRowIndex();
        int ci = cell.getColumnIndex();
        ExcelCell excelCell;
        while (--ci >= 0) {
            excelCell = map.getOrDefault(ri, Collections.emptyMap()).get(ci);
            if (excelCell != null) return excelCell;
        }
        return null;
    }

    private static IExcelCell getTopCell(IExcelCell cell,
            Map<Integer, Map<Integer, ExcelCell>> map) {
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

    public void addCell(IExcelCell cell) {
        cells.add(cell);
    }
}
