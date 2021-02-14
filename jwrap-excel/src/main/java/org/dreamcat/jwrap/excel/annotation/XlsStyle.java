package org.dreamcat.jwrap.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Create by tuke on 2020/7/23
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsStyle {

    HorizontalAlignment horizontalAlignment() default HorizontalAlignment.RIGHT;

    VerticalAlignment verticalAlignment() default VerticalAlignment.CENTER;

    boolean hidden() default false;

    boolean wrapText() default false;

    boolean locked() default false;

    boolean quotePrefix() default false;

    boolean shrinkToFit() default false;

    /// rich style

    short indent() default -1;

    // HSSF uses values from -90 to 90 degrees,
    // whereas XSSF uses values from 0 to 180 degrees
    short rotation() default 0;

    /**
     * @return filled background color
     * @see IndexedColors
     */
    short bgColor() default -1;

    short fgColor() default -1;

    IndexedColors bgIndexedColor() default IndexedColors.AUTOMATIC;

    IndexedColors fgIndexedColor() default IndexedColors.AUTOMATIC;

    FillPatternType fillPattern() default FillPatternType.SOLID_FOREGROUND;

    BorderStyle borderBottom() default BorderStyle.NONE;

    BorderStyle borderLeft() default BorderStyle.NONE;

    BorderStyle borderTop() default BorderStyle.NONE;

    BorderStyle borderRight() default BorderStyle.NONE;

    short bottomBorderColor() default -1;

    short leftBorderColor() default -1;

    short topBorderColor() default -1;

    short rightBorderColor() default -1;

    IndexedColors bottomBorderIndexedColor() default IndexedColors.AUTOMATIC;

    IndexedColors leftBorderIndexedColor() default IndexedColors.AUTOMATIC;

    IndexedColors topBorderIndexedColor() default IndexedColors.AUTOMATIC;

    IndexedColors rightBorderIndexedColor() default IndexedColors.AUTOMATIC;
}
