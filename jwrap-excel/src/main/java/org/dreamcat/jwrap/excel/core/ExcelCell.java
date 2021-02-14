package org.dreamcat.jwrap.excel.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.style.ExcelComment;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@NoArgsConstructor
public class ExcelCell implements IExcelCell {

    protected IExcelContent content;
    protected int rowIndex;
    protected int columnIndex;
    protected CellPart cellPart;

    public ExcelCell(IExcelContent content, int rowIndex, int columnIndex) {
        this.content = content;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public ExcelCell(IExcelContent content, int rowIndex, int columnIndex,
            int rowSpan, int columnSpan) {
        this.content = content;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        if (rowSpan > 1 || columnSpan > 1) {
            this.cellPart = new CellPart(rowSpan, columnSpan);
        }
    }

    @Override
    public int getRowSpan() {
        return cellPart != null ? cellPart.rowSpan : 1;
    }

    @Override
    public void setRowSpan(int rowSpan) {
        if (cellPart == null) cellPart = new CellPart();
        cellPart.rowSpan = rowSpan;
    }

    @Override
    public int getColumnSpan() {
        return cellPart != null ? cellPart.columnSpan : 1;
    }

    @Override
    public void setColumnSpan(int columnSpan) {
        if (cellPart == null) cellPart = new CellPart();
        cellPart.columnSpan = columnSpan;
    }

    @Override
    public int getFontIndex() {
        return cellPart != null ? cellPart.fontIndex : -1;
    }

    @Override
    public int getStyleIndex() {
        return cellPart != null ? cellPart.styleIndex : -1;
    }

    @Override
    public ExcelHyperLink getHyperLink() {
        return cellPart != null ? cellPart.hyperLink : null;
    }

    @Override
    public ExcelComment getComment() {
        return cellPart != null ? cellPart.comment : null;
    }

    @Data
    @NoArgsConstructor
    public static class CellPart {

        public int rowSpan = 1;
        public int columnSpan = 1;
        protected int fontIndex = -1;
        protected int styleIndex = -1;
        protected ExcelHyperLink hyperLink;
        protected ExcelComment comment;

        public CellPart(int rowSpan, int columnSpan) {
            this.rowSpan = rowSpan;
            this.columnSpan = columnSpan;
        }
    }

    public ExcelCell fontIndex(int fontIndex) {
        if (cellPart == null) cellPart = new CellPart();
        cellPart.fontIndex = fontIndex;
        return this;
    }

    public ExcelCell styleIndex(int styleIndex) {
        if (cellPart == null) cellPart = new CellPart();
        cellPart.styleIndex = styleIndex;
        return this;
    }

    public ExcelCell hyperLink(ExcelHyperLink hyperLink) {
        if (cellPart == null) cellPart = new CellPart();
        cellPart.hyperLink = hyperLink;
        return this;
    }

    public ExcelCell comment(ExcelComment comment) {
        if (cellPart == null) cellPart = new CellPart();
        cellPart.comment = comment;
        return this;
    }
}
