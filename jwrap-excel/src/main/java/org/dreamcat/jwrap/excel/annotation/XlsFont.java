package org.dreamcat.jwrap.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Create by tuke on 2020/7/23
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsFont {

    String name();

    boolean bold() default false;

    boolean italic() default false;

    byte underline() default -1;

    boolean strikeout() default false;

    short typeOffset() default -1;

    short color() default -1;

    IndexedColors indexedColor() default IndexedColors.AUTOMATIC;

    short height() default -1;
}
