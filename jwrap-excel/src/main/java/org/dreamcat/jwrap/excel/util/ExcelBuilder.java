package org.dreamcat.jwrap.excel.util;

import lombok.RequiredArgsConstructor;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.x.asm.BeanCopierUtil;
import org.dreamcat.jwrap.excel.content.ExcelNumericContent;
import org.dreamcat.jwrap.excel.content.ExcelStringContent;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.ExcelCell;
import org.dreamcat.jwrap.excel.core.ExcelRichCell;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.content.ExcelBooleanContent;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;
import org.dreamcat.jwrap.excel.style.ExcelRichStyle;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/22
 */
public final class ExcelBuilder {

    private ExcelBuilder() {
    }

    public static SheetTerm sheet(String sheetName) {
        return new SheetTerm(new org.dreamcat.jwrap.excel.core.ExcelSheet(sheetName));
    }

    public static WorkbookTerm workbook() {
        ExcelWorkbook<org.dreamcat.jwrap.excel.core.ExcelSheet> book = new ExcelWorkbook<>();
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
            return new ExcelStringContent(value == null ? "" : value.toString());
        }
    }

    public static IExcelContent term(String string) {
        return new ExcelStringContent(string);
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
    public static class SheetTerm {

        private final org.dreamcat.jwrap.excel.core.ExcelSheet sheet;

        public org.dreamcat.jwrap.excel.core.ExcelSheet finishSheet() {
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

        public RichSheetTerm richCell(String string, int rowIndex, int columnIndex) {
            return richCell(term(string), rowIndex, columnIndex, 1, 1);
        }

        public RichSheetTerm richCell(double number, int rowIndex, int columnIndex) {
            return richCell(term(number), rowIndex, columnIndex, 1, 1);
        }

        public RichSheetTerm richCell(IExcelContent term, int rowIndex, int columnIndex) {
            return richCell(term, rowIndex, columnIndex, 1, 1);
        }

        public RichSheetTerm richCell(String string, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return richCell(term(string), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public RichSheetTerm richCell(double number, int rowIndex, int columnIndex, int rowSpan,
                int columnSpan) {
            return richCell(term(number), rowIndex, columnIndex, rowSpan, columnSpan);
        }

        public RichSheetTerm richCell(IExcelContent term, int rowIndex, int columnIndex,
                int rowSpan, int columnSpan) {
            ExcelRichCell cell = new ExcelRichCell(term, rowIndex, columnIndex, rowSpan,
                    columnSpan);
            sheet.getCells().add(cell);
            return new RichSheetTerm(this, cell);
        }
    }

    public static class RichSheetTerm {

        private final SheetTerm sheetTerm;
        private final ExcelRichCell cell;
        private org.dreamcat.jwrap.excel.style.ExcelFont font;
        private org.dreamcat.jwrap.excel.style.ExcelStyle style;
        private org.dreamcat.jwrap.excel.style.ExcelRichStyle richStyle;

        public RichSheetTerm(SheetTerm sheetTerm, ExcelRichCell cell) {
            this.sheetTerm = sheetTerm;
            this.cell = cell;
        }

        public SheetTerm finishCell() {
            if (richStyle != null) cell.setStyle(richStyle);
            else if (style != null) cell.setStyle(style);
            if (font != null) cell.setFont(font);
            return sheetTerm;
        }

        public RichSheetTerm hyperLink(String address) {
            return hyperLink(address, null);
        }

        public RichSheetTerm hyperLink(String address, String label) {
            return hyperLink(address, label, HyperlinkType.URL);
        }

        public RichSheetTerm hyperLink(String address, String label, HyperlinkType type) {
            cell.setHyperLink(new ExcelHyperLink(type, address, label));
            return this;
        }

        public RichSheetTerm bold() {
            return bold(true);
        }

        public RichSheetTerm bold(boolean bold) {
            getFont().setBold(bold);
            return this;
        }

        public RichSheetTerm italic() {
            return italic(true);
        }

        public RichSheetTerm italic(boolean italic) {
            getFont().setItalic(italic);
            return this;
        }

        public RichSheetTerm underline() {
            return underline(Font.U_SINGLE);
        }

        public RichSheetTerm underline(byte underline) {
            getFont().setUnderline(underline);
            return this;
        }

        public RichSheetTerm strikeout() {
            return strikeout(true);
        }

        public RichSheetTerm strikeout(boolean strikeout) {
            getFont().setStrikeout(strikeout);
            return this;
        }

        public RichSheetTerm typeOffset() {
            return typeOffset(Font.SS_NONE);
        }

        public RichSheetTerm typeOffset(short typeOffset) {
            getFont().setTypeOffset(typeOffset);
            return this;
        }

        public RichSheetTerm color() {
            return color(Font.COLOR_NORMAL);
        }

        public RichSheetTerm color(short color) {
            getFont().setColor(color);
            return this;
        }

        public RichSheetTerm height(int height) {
            getFont().setHeight((short) height);
            return this;
        }

        public RichSheetTerm horizontalAlignment(HorizontalAlignment horizontalAlignment) {
            getStyle().setHorizontalAlignment(horizontalAlignment);
            return this;
        }

        public RichSheetTerm verticalAlignment(VerticalAlignment verticalAlignment) {
            getStyle().setVerticalAlignment(verticalAlignment);
            return this;
        }

        public RichSheetTerm hidden() {
            return hidden(true);
        }

        public RichSheetTerm hidden(boolean hidden) {
            getStyle().setHidden(hidden);
            return this;
        }

        public RichSheetTerm wrapText() {
            return wrapText(true);
        }

        public RichSheetTerm wrapText(boolean wrapText) {
            getStyle().setWrapText(wrapText);
            return this;
        }

        public RichSheetTerm locked() {
            return locked(true);
        }

        public RichSheetTerm locked(boolean locked) {
            getStyle().setLocked(locked);
            return this;
        }

        public RichSheetTerm quotePrefix() {
            return quotePrefix(true);
        }

        public RichSheetTerm quotePrefix(boolean quotePrefix) {
            getStyle().setQuotePrefix(quotePrefix);
            return this;
        }

        public RichSheetTerm shrinkToFit() {
            return shrinkToFit(true);
        }

        public RichSheetTerm shrinkToFit(boolean shrinkToFit) {
            getStyle().setShrinkToFit(shrinkToFit);
            return this;
        }

        // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

        public RichSheetTerm rotation(short rotation) {
            getRichStyle().setRotation(rotation);
            return this;
        }

        public RichSheetTerm bgColor(short bgColor) {
            getRichStyle().setBgColor(bgColor);
            return this;
        }

        public RichSheetTerm fgColor(short fgColor) {
            getRichStyle().setFgColor(fgColor);
            return this;
        }

        public RichSheetTerm fillPattern(FillPatternType fillPatternType) {
            getRichStyle().setFillPattern(fillPatternType);
            return this;
        }

        public RichSheetTerm borderBottom(BorderStyle borderBottom) {
            getRichStyle().setBorderBottom(borderBottom);
            return this;
        }

        public RichSheetTerm borderLeft(BorderStyle borderLeft) {
            getRichStyle().setBorderLeft(borderLeft);
            return this;
        }

        public RichSheetTerm borderTop(BorderStyle borderTop) {
            getRichStyle().setBorderTop(borderTop);
            return this;
        }

        public RichSheetTerm borderRight(BorderStyle borderRight) {
            getRichStyle().setBorderRight(borderRight);
            return this;
        }

        public RichSheetTerm bottomBorderColor(short bottomBorderColor) {
            getRichStyle().setBottomBorderColor(bottomBorderColor);
            return this;
        }

        public RichSheetTerm leftBorderColor(short leftBorderColor) {
            getRichStyle().setLeftBorderColor(leftBorderColor);
            return this;
        }

        public RichSheetTerm topBorderColor(short topBorderColor) {
            getRichStyle().setTopBorderColor(topBorderColor);
            return this;
        }

        public RichSheetTerm rightBorderColor(short rightBorderColor) {
            getRichStyle().setRightBorderColor(rightBorderColor);
            return this;
        }

        // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

        private org.dreamcat.jwrap.excel.style.ExcelFont getFont() {
            if (font == null) {
                font = new ExcelFont();
            }
            return font;
        }

        private org.dreamcat.jwrap.excel.style.ExcelStyle getStyle() {
            if (richStyle != null) return richStyle;
            if (style != null) return style;
            style = new ExcelStyle();
            return style;
        }

        private org.dreamcat.jwrap.excel.style.ExcelRichStyle getRichStyle() {
            if (richStyle == null) {
                if (style == null) {
                    richStyle = new org.dreamcat.jwrap.excel.style.ExcelRichStyle();
                } else {
                    richStyle = BeanCopierUtil.copy(style, ExcelRichStyle.class);
                    style = null;
                }
            }
            return richStyle;
        }

    }

    @RequiredArgsConstructor
    public static class WorkbookTerm {

        private final ExcelWorkbook<org.dreamcat.jwrap.excel.core.ExcelSheet> book;

        public WorkbookTerm addSheet(org.dreamcat.jwrap.excel.core.ExcelSheet sheet) {
            book.getSheets().add(sheet);
            return this;
        }

        public ExcelWorkbook<ExcelSheet> finish() {
            return book;
        }

    }

}
