package org.dreamcat.jwrap.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by tuke on 2021/2/22
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsHeader {

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Default {

        XlsFont font() default @XlsFont();

        XlsStyle style() default @XlsStyle();

        boolean onlyAnnotated() default false;
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface SubheaderStyle {

        boolean enabled() default true;

        XlsFont font() default @XlsFont();

        XlsStyle style() default @XlsStyle();
    }

    boolean ignored() default false;

    // header
    String header() default "";

    // header style
    XlsFont font() default @XlsFont();

    XlsStyle style() default @XlsStyle();

    // enable subheader when it is expanded
    boolean subheader() default false;

    boolean subheaderInherited() default false;

    SubheaderStyle subheaderStyle() default @SubheaderStyle(enabled = false);

    int fieldIndex() default -1;

    boolean expanded() default false;
}
