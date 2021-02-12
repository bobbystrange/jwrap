package org.dreamcat.jwrap.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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


}
