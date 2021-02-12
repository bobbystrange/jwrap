package org.dreamcat.jwrap.excel.style;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;

/**
 * Create by tuke on 2020/7/21
 *
 * @see org.apache.poi.ss.usermodel.IndexedColors
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExcelRichStyle extends ExcelStyle {

    private short indent;
    // HSSF uses values from -90 to 90 degrees,
    // whereas XSSF uses values from 0 to 180 degrees
    private short rotation;

    private short bgColor;
    private short fgColor;
    private FillPatternType fillPattern;

    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderTop;
    private BorderStyle borderRight;

    private short bottomBorderColor;
    private short leftBorderColor;
    private short topBorderColor;
    private short rightBorderColor;

    public static boolean hasRichStyle(CellStyle style) {
        return style.getIndention() != 0 ||
                style.getRotation() != 0 ||

                style.getFillBackgroundColor() != 0 ||
                style.getFillForegroundColor() != 0 ||
                style.getFillPattern() != null ||

                style.getBorderBottom() != null ||
                style.getBorderLeft() != null ||
                style.getBorderTop() != null ||
                style.getBorderRight() != null ||

                style.getBottomBorderColor() != 0 ||
                style.getLeftBorderColor() != 0 ||
                style.getTopBorderColor() != 0 ||
                style.getRightBorderColor() != 0;
    }

    @Override
    public void fill(CellStyle style, Font font) {
        super.fill(style, font);

        if (indent > 0) style.setIndention(indent);
        if (rotation > 0) style.setRotation(rotation);

        if (bgColor != 0) style.setFillBackgroundColor(bgColor);
        if (fgColor != 0) style.setFillForegroundColor(fgColor);
        if (fillPattern != null) style.setFillPattern(fillPattern);

        if (borderBottom != null) style.setBorderBottom(borderBottom);
        if (borderLeft != null) style.setBorderLeft(borderLeft);
        if (borderTop != null) style.setBorderTop(borderTop);
        if (borderRight != null) style.setBorderRight(borderRight);

        if (bottomBorderColor != 0) style.setBottomBorderColor(bottomBorderColor);
        if (leftBorderColor != 0) style.setLeftBorderColor(leftBorderColor);
        if (topBorderColor != 0) style.setTopBorderColor(topBorderColor);
        if (rightBorderColor != 0) style.setRightBorderColor(rightBorderColor);
    }
}
