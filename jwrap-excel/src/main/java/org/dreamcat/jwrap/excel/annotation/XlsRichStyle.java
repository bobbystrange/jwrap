package org.dreamcat.jwrap.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Create by tuke on 2020/7/24
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsRichStyle {

    short indent() default -1;

    // HSSF uses values from -90 to 90 degrees,
    // whereas XSSF uses values from 0 to 180 degrees
    short rotation() default -1;

    short bgColor() default -1;

    short fgColor() default -1;

    IndexedColors bgIndexedColor() default IndexedColors.WHITE;

    IndexedColors fgIndexedColor() default IndexedColors.BLACK;

    FillPatternType fillPattern() default FillPatternType.SOLID_FOREGROUND;

    BorderStyle borderBottom() default BorderStyle.NONE;

    BorderStyle borderLeft() default BorderStyle.NONE;

    BorderStyle borderTop() default BorderStyle.NONE;

    BorderStyle borderRight() default BorderStyle.NONE;

    short bottomBorderColor() default -1;

    short leftBorderColor() default -1;

    short topBorderColor() default -1;

    short rightBorderColor() default -1;

    IndexedColors bottomBorderIndexedColor() default IndexedColors.BLACK;

    IndexedColors leftBorderIndexedColor() default IndexedColors.BLACK;

    IndexedColors topBorderIndexedColor() default IndexedColors.BLACK;

    IndexedColors rightBorderIndexedColor() default IndexedColors.BLACK;
}
