package org.dreamcat.jwrap.excel.util;

import lombok.RequiredArgsConstructor;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.jwrap.excel.content.ExcelBooleanContent;
import org.dreamcat.jwrap.excel.content.ExcelNumericContent;
import org.dreamcat.jwrap.excel.content.ExcelStringContent;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.ExcelCell;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/22
 */
public final class ExcelBuilder {

    private ExcelBuilder() {
    }

    public static SheetTerm sheet(String sheetName) {
        return new SheetTerm(new ExcelSheet(sheetName));
    }

    public static WorkbookTerm workbook() {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();
        return new WorkbookTerm(book);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static IExcelContent term(Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            return new ExcelNumericContent(number.doubleValue());
        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean) value;
            return new ExcelBooleanContent(bool);
        } else if (value instanceof IExcelContent) {
            return (IExcelContent) value;
        } else {
            return ExcelStringContent.from(value == null ? "" : value.toString());
        }
    }

    public static IExcelContent term(String string) {
        return ExcelStringContent.from(string);
    }

    public static IExcelContent term(double number) {
        return new ExcelNumericContent(number);
    }

    public static ExcelCell term(String string, int rowIndex, int columnIndex) {
        return new ExcelCell(term(string), rowIndex, columnIndex);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static ExcelCell term(String string, int rowIndex, int columnIndex, int rowSpan,
            int columnSpan) {
        return new ExcelCell(term(string), rowIndex, columnIndex, rowSpan, columnSpan);
    }

    @RequiredArgsConstructor
    public static class WorkbookTerm {

        private final ExcelWorkbook<ExcelSheet> book;

        public WorkbookTerm sheet(SheetTerm sheetTerm) {
            return sheet(sheetTerm.finish());
        }

        public WorkbookTerm sheet(ExcelSheet sheet) {
            book.getSheets().add(sheet);
            return this;
        }

        public ExcelWorkbook<ExcelSheet> finish() {
            return book;
        }
    }

    @RequiredArgsConstructor
    public static class SheetTerm {

        private final ExcelSheet sheet;

        public ExcelSheet finish() {
            return sheet;
        }

        public SheetTerm cell(IExcelCell cell) {
            sheet.getCells().add(cell);
            return this;
        }

        public SheetTerm cell(IExcelContent term, int rowIndex, int columnIndex) {
            return cell(term, rowIndex, columnIndex, 1, 1);
        }

        public SheetTerm cell(double number, int rowIndex, int columnIndex) {
            return cell(term(number), rowIndex, columnIndex);
        }

        public SheetTerm cell(double number, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return cell(term(number), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public SheetTerm cell(String string, int rowIndex, int columnIndex) {
            return cell(term(string), rowIndex, columnIndex);
        }

        public SheetTerm cell(String string, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return cell(term(string), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public SheetTerm cell(IExcelContent term, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            sheet.getCells().add(new ExcelCell(term, rowIndex, columnIndex, rowSpan, columnSpan));
            return this;
        }

        public CellTerm richCell(String string, int rowIndex, int columnIndex) {
            return richCell(term(string), rowIndex, columnIndex, 1, 1);
        }

        public CellTerm richCell(double number, int rowIndex, int columnIndex) {
            return richCell(term(number), rowIndex, columnIndex, 1, 1);
        }

        public CellTerm richCell(IExcelContent term, int rowIndex, int columnIndex) {
            return richCell(term, rowIndex, columnIndex, 1, 1);
        }

        public CellTerm richCell(String string, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return richCell(term(string), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public CellTerm richCell(double number, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return richCell(term(number), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public CellTerm richCell(IExcelContent term, int rowIndex, int columnIndex,
                int rowSpan, int columnSpan) {
            ExcelCell cell = new ExcelCell(term, rowIndex, columnIndex, rowSpan,
                    columnSpan);
            sheet.getCells().add(cell);
            return new CellTerm(this, cell);
        }
    }

    public static class CellTerm {

        private final SheetTerm sheetTerm;
        private final ExcelCell cell;
        private ExcelFont font;
        private ExcelStyle style;

        public CellTerm(SheetTerm sheetTerm, ExcelCell cell) {
            this.sheetTerm = sheetTerm;
            this.cell = cell;
        }

        public SheetTerm finishCell() {
            if (style == null && font == null) {
                return sheetTerm;
            }
            if (style == null) style = new ExcelStyle();
            if (font != null) style.setFont(font);
            cell.setStyle(style);
            return sheetTerm;
        }

        public CellTerm hyperLink(String address) {
            return hyperLink(address, null);
        }

        public CellTerm hyperLink(String address, String label) {
            return hyperLink(address, label, HyperlinkType.URL);
        }

        public CellTerm hyperLink(String address, String label, HyperlinkType type) {
            cell.setHyperLink(new ExcelHyperLink(type, address, label));
            return this;
        }

        public CellTerm bold() {
            return bold(true);
        }

        public CellTerm bold(boolean bold) {
            getFont().setBold(bold);
            return this;
        }

        public CellTerm italic() {
            return italic(true);
        }

        public CellTerm italic(boolean italic) {
            getFont().setItalic(italic);
            return this;
        }

        public CellTerm underline() {
            return underline(Font.U_SINGLE);
        }

        public CellTerm underline(byte underline) {
            getFont().setUnderline(underline);
            return this;
        }

        public CellTerm strikeout() {
            return strikeout(true);
        }

        public CellTerm strikeout(boolean strikeout) {
            getFont().setStrikeout(strikeout);
            return this;
        }

        public CellTerm typeOffset() {
            return typeOffset(Font.SS_NONE);
        }

        public CellTerm typeOffset(short typeOffset) {
            getFont().setTypeOffset(typeOffset);
            return this;
        }

        public CellTerm color() {
            return color(Font.COLOR_NORMAL);
        }

        public CellTerm color(short color) {
            getFont().setColor(color);
            return this;
        }

        public CellTerm height(int height) {
            getFont().setHeight((short) height);
            return this;
        }

        public CellTerm horizontalAlignment(HorizontalAlignment horizontalAlignment) {
            getStyle().setHorizontalAlignment(horizontalAlignment);
            return this;
        }

        public CellTerm verticalAlignment(VerticalAlignment verticalAlignment) {
            getStyle().setVerticalAlignment(verticalAlignment);
            return this;
        }

        public CellTerm hidden() {
            return hidden(true);
        }

        public CellTerm hidden(boolean hidden) {
            getStyle().setHidden(hidden);
            return this;
        }

        public CellTerm wrapText() {
            return wrapText(true);
        }

        public CellTerm wrapText(boolean wrapText) {
            getStyle().setWrapText(wrapText);
            return this;
        }

        public CellTerm locked() {
            return locked(true);
        }

        public CellTerm locked(boolean locked) {
            getStyle().setLocked(locked);
            return this;
        }

        public CellTerm quotePrefix() {
            return quotePrefix(true);
        }

        public CellTerm quotePrefix(boolean quotePrefix) {
            getStyle().setQuotePrefix(quotePrefix);
            return this;
        }

        public CellTerm shrinkToFit() {
            return shrinkToFit(true);
        }

        public CellTerm shrinkToFit(boolean shrinkToFit) {
            getStyle().setShrinkToFit(shrinkToFit);
            return this;
        }

        // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

        public CellTerm rotation(short rotation) {
            getStyle().setRotation(rotation);
            return this;
        }

        public CellTerm bgColor(short bgColor) {
            getStyle().setBgColor(bgColor);
            return this;
        }

        public CellTerm fgColor(short fgColor) {
            getStyle().setFgColor(fgColor);
            return this;
        }

        public CellTerm fillPattern(FillPatternType fillPatternType) {
            getStyle().setFillPattern(fillPatternType);
            return this;
        }

        public CellTerm borderBottom(BorderStyle borderBottom) {
            getStyle().setBorderBottom(borderBottom);
            return this;
        }

        public CellTerm borderLeft(BorderStyle borderLeft) {
            getStyle().setBorderLeft(borderLeft);
            return this;
        }

        public CellTerm borderTop(BorderStyle borderTop) {
            getStyle().setBorderTop(borderTop);
            return this;
        }

        public CellTerm borderRight(BorderStyle borderRight) {
            getStyle().setBorderRight(borderRight);
            return this;
        }

        public CellTerm bottomBorderColor(short bottomBorderColor) {
            getStyle().setBottomBorderColor(bottomBorderColor);
            return this;
        }

        public CellTerm leftBorderColor(short leftBorderColor) {
            getStyle().setLeftBorderColor(leftBorderColor);
            return this;
        }

        public CellTerm topBorderColor(short topBorderColor) {
            getStyle().setTopBorderColor(topBorderColor);
            return this;
        }

        public CellTerm rightBorderColor(short rightBorderColor) {
            getStyle().setRightBorderColor(rightBorderColor);
            return this;
        }

        // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

        private ExcelFont getFont() {
            if (font == null) font = new ExcelFont();
            return font;
        }

        private ExcelStyle getStyle() {
            if (style == null) style = new ExcelStyle();
            return style;
        }
    }

}
