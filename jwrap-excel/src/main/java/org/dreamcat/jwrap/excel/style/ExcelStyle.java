package org.dreamcat.jwrap.excel.style;

import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.jwrap.excel.annotation.XlsRichStyle;
import org.dreamcat.jwrap.excel.annotation.XlsStyle;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelStyle {

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private boolean hidden;
    private boolean wrapText;
    private boolean locked;

    private boolean quotePrefix;
    // Controls if the Cell should be auto-sized to shrink to fit if the text is too long
    private boolean shrinkToFit;

    public static ExcelStyle from(CellStyle style) {
        ExcelStyle excelStyle = null;
        if (org.dreamcat.jwrap.excel.style.ExcelRichStyle.hasRichStyle(style)) {
            org.dreamcat.jwrap.excel.style.ExcelRichStyle excelRichStyle = new org.dreamcat.jwrap.excel.style.ExcelRichStyle();

            excelRichStyle.setIndent(style.getIndention());
            excelRichStyle.setRotation(style.getIndention());

            excelRichStyle.setBgColor(style.getFillBackgroundColor());
            excelRichStyle.setFgColor(style.getFillForegroundColor());
            excelRichStyle.setFillPattern(style.getFillPattern());

            excelRichStyle.setBorderBottom(style.getBorderBottom());
            excelRichStyle.setBorderLeft(style.getBorderLeft());
            excelRichStyle.setBorderTop(style.getBorderTop());
            excelRichStyle.setBorderRight(style.getBorderRight());

            excelRichStyle.setBottomBorderColor(style.getBottomBorderColor());
            excelRichStyle.setLeftBorderColor(style.getLeftBorderColor());
            excelRichStyle.setTopBorderColor(style.getTopBorderColor());
            excelRichStyle.setRightBorderColor(style.getRightBorderColor());

            excelStyle = excelRichStyle;
        }

        if (excelStyle == null) {
            excelStyle = new ExcelStyle();
        }

        excelStyle.setHorizontalAlignment(style.getAlignment());
        excelStyle.setVerticalAlignment(style.getVerticalAlignment());
        excelStyle.setHidden(style.getHidden());
        excelStyle.setHidden(style.getWrapText());
        excelStyle.setHidden(style.getLocked());
        excelStyle.setQuotePrefix(style.getQuotePrefixed());
        excelStyle.setShrinkToFit(style.getShrinkToFit());

        return excelStyle;
    }

    public static ExcelStyle from(XlsStyle xlsStyle, XlsRichStyle xlsRichStyle) {
        ExcelStyle style;
        if (xlsRichStyle == null) {
            style = new ExcelStyle();
        } else {
            org.dreamcat.jwrap.excel.style.ExcelRichStyle richStyle = new ExcelRichStyle();

            if (xlsRichStyle.indent() != -1) richStyle.setIndent(xlsRichStyle.indent());
            if (xlsRichStyle.rotation() != -1) richStyle.setRotation(xlsRichStyle.rotation());

            if (xlsRichStyle.bgColor() != -1) {
                richStyle.setBgColor(xlsRichStyle.bgColor());
            } else {
                richStyle.setBgColor(xlsRichStyle.bgIndexedColor().getIndex());
            }
            if (xlsRichStyle.fgColor() != -1) {
                richStyle.setFgColor(xlsRichStyle.fgColor());
            } else {
                richStyle.setFgColor(xlsRichStyle.fgIndexedColor().getIndex());
            }
            richStyle.setFillPattern(xlsRichStyle.fillPattern());

            richStyle.setBorderBottom(xlsRichStyle.borderBottom());
            richStyle.setBorderLeft(xlsRichStyle.borderLeft());
            richStyle.setBorderTop(xlsRichStyle.borderTop());
            richStyle.setBorderRight(xlsRichStyle.borderRight());

            if (xlsRichStyle.bottomBorderColor() != -1) {
                richStyle.setBottomBorderColor(xlsRichStyle.bottomBorderColor());
            } else {
                richStyle.setBottomBorderColor(xlsRichStyle.bottomBorderIndexedColor().getIndex());
            }
            if (xlsRichStyle.leftBorderColor() != -1) {
                richStyle.setLeftBorderColor(xlsRichStyle.leftBorderColor());
            } else {
                richStyle.setLeftBorderColor(xlsRichStyle.leftBorderIndexedColor().getIndex());
            }
            if (xlsRichStyle.topBorderColor() != -1) {
                richStyle.setTopBorderColor(xlsRichStyle.topBorderColor());
            } else {
                richStyle.setTopBorderColor(xlsRichStyle.topBorderIndexedColor().getIndex());
            }
            if (xlsRichStyle.rightBorderColor() != -1) {
                richStyle.setRightBorderColor(xlsRichStyle.rightBorderColor());
            } else {
                richStyle.setRightBorderColor(xlsRichStyle.rightBorderIndexedColor().getIndex());
            }

            style = richStyle;
        }

        if (xlsStyle == null) return style;

        style.setHorizontalAlignment(xlsStyle.horizontalAlignment());
        style.setVerticalAlignment(xlsStyle.verticalAlignment());

        style.setHidden(xlsStyle.hidden());
        style.setWrapText(xlsStyle.wrapText());
        style.setLocked(xlsStyle.locked());

        style.setQuotePrefix(xlsStyle.quotePrefix());
        style.setShrinkToFit(xlsStyle.shrinkToFit());

        return style;
    }

    public void fill(CellStyle style, Font font) {
        if (horizontalAlignment != null) style.setAlignment(horizontalAlignment);
        if (verticalAlignment != null) style.setVerticalAlignment(verticalAlignment);
        style.setLocked(locked);

        style.setQuotePrefixed(quotePrefix);
        style.setShrinkToFit(shrinkToFit);
        style.setHidden(hidden);
        style.setWrapText(wrapText);
        if (font != null) style.setFont(font);
    }

}
