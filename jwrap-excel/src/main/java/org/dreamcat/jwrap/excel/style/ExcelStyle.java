package org.dreamcat.jwrap.excel.style;

import lombok.Data;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.jwrap.excel.annotation.XlsStyle;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelStyle {

    private int index = -1;
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private boolean hidden;
    private boolean wrapText;
    private boolean locked;

    private boolean quotePrefix;
    // Controls if the Cell should be auto-sized to shrink to fit if the text is too long
    private boolean shrinkToFit;

    /// rich style

    private short indent = -1;
    // HSSF uses values from -90 to 90 degrees,
    // whereas XSSF uses values from 0 to 180 degrees
    private short rotation = 0;

    private short bgColor = -1;
    private short fgColor = -1;
    private FillPatternType fillPattern;

    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderTop;
    private BorderStyle borderRight;

    private short bottomBorderColor = -1;
    private short leftBorderColor = -1;
    private short topBorderColor = -1;
    private short rightBorderColor = -1;

    public static ExcelStyle from(CellStyle style) {
        ExcelStyle excelStyle = new ExcelStyle();
        excelStyle.setIndex(style.getIndex());
        excelStyle.setHorizontalAlignment(style.getAlignment());
        excelStyle.setVerticalAlignment(style.getVerticalAlignment());
        excelStyle.setHidden(style.getHidden());
        excelStyle.setHidden(style.getWrapText());
        excelStyle.setHidden(style.getLocked());
        excelStyle.setQuotePrefix(style.getQuotePrefixed());
        excelStyle.setShrinkToFit(style.getShrinkToFit());
        // rich style
        excelStyle.setIndent(style.getIndention());
        excelStyle.setRotation(style.getIndention());
        excelStyle.setBgColor(style.getFillBackgroundColor());
        excelStyle.setFgColor(style.getFillForegroundColor());
        excelStyle.setFillPattern(style.getFillPattern());
        excelStyle.setBorderBottom(style.getBorderBottom());
        excelStyle.setBorderLeft(style.getBorderLeft());
        excelStyle.setBorderTop(style.getBorderTop());
        excelStyle.setBorderRight(style.getBorderRight());
        excelStyle.setBottomBorderColor(style.getBottomBorderColor());
        excelStyle.setLeftBorderColor(style.getLeftBorderColor());
        excelStyle.setTopBorderColor(style.getTopBorderColor());
        excelStyle.setRightBorderColor(style.getRightBorderColor());
        return excelStyle;
    }

    public static ExcelStyle from(XlsStyle xlsStyle) {
        ExcelStyle style = new ExcelStyle();
        style.setHorizontalAlignment(xlsStyle.horizontalAlignment());
        style.setVerticalAlignment(xlsStyle.verticalAlignment());
        style.setHidden(xlsStyle.hidden());
        style.setWrapText(xlsStyle.wrapText());
        style.setLocked(xlsStyle.locked());
        style.setQuotePrefix(xlsStyle.quotePrefix());
        style.setShrinkToFit(xlsStyle.shrinkToFit());
        // rich style
        style.setIndent(xlsStyle.indent());
        style.setRotation(xlsStyle.rotation());

        if (xlsStyle.bgColor() != -1) {
            style.setBgColor(xlsStyle.bgColor());
        } else {
            style.setBgColor(xlsStyle.bgIndexedColor().getIndex());
        }
        if (xlsStyle.fgColor() != -1) {
            style.setFgColor(xlsStyle.fgColor());
        } else {
            style.setFgColor(xlsStyle.fgIndexedColor().getIndex());
        }
        style.setFillPattern(xlsStyle.fillPattern());

        style.setBorderBottom(xlsStyle.borderBottom());
        style.setBorderLeft(xlsStyle.borderLeft());
        style.setBorderTop(xlsStyle.borderTop());
        style.setBorderRight(xlsStyle.borderRight());

        if (xlsStyle.bottomBorderColor() != -1) {
            style.setBottomBorderColor(xlsStyle.bottomBorderColor());
        } else {
            style.setBottomBorderColor(xlsStyle.bottomBorderIndexedColor().getIndex());
        }
        if (xlsStyle.leftBorderColor() != -1) {
            style.setLeftBorderColor(xlsStyle.leftBorderColor());
        } else {
            style.setLeftBorderColor(xlsStyle.leftBorderIndexedColor().getIndex());
        }
        if (xlsStyle.topBorderColor() != -1) {
            style.setTopBorderColor(xlsStyle.topBorderColor());
        } else {
            style.setTopBorderColor(xlsStyle.topBorderIndexedColor().getIndex());
        }
        if (xlsStyle.rightBorderColor() != -1) {
            style.setRightBorderColor(xlsStyle.rightBorderColor());
        } else {
            style.setRightBorderColor(xlsStyle.rightBorderIndexedColor().getIndex());
        }
        return style;
    }

    public void fill(CellStyle style) {
        if (horizontalAlignment != null) style.setAlignment(horizontalAlignment);
        if (verticalAlignment != null) style.setVerticalAlignment(verticalAlignment);
        style.setLocked(locked);
        style.setQuotePrefixed(quotePrefix);
        style.setShrinkToFit(shrinkToFit);
        style.setHidden(hidden);
        style.setWrapText(wrapText);

        if (indent != -1) style.setIndention(indent);
        if (rotation != 0) style.setRotation(rotation);
        if (hasColor(fgColor) || hasColor(bgColor)) {
            if (hasColor(fgColor)) style.setFillForegroundColor(fgColor);
            if (hasColor(bgColor)) style.setFillBackgroundColor(bgColor);
            if (fillPattern != null) style.setFillPattern(fillPattern);
        }

        if (borderBottom != null) style.setBorderBottom(borderBottom);
        if (borderLeft != null) style.setBorderLeft(borderLeft);
        if (borderTop != null) style.setBorderTop(borderTop);
        if (borderRight != null) style.setBorderRight(borderRight);

        if (hasColor(bottomBorderColor)) style.setBottomBorderColor(bottomBorderColor);
        if (hasColor(leftBorderColor)) style.setLeftBorderColor(leftBorderColor);
        if (hasColor(topBorderColor)) style.setTopBorderColor(topBorderColor);
        if (hasColor(rightBorderColor)) style.setRightBorderColor(rightBorderColor);
    }

    private static boolean hasColor(int color) {
        return color != -1 && color != IndexedColors.AUTOMATIC.getIndex();
    }

}
