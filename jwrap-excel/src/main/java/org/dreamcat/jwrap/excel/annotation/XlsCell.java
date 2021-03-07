package org.dreamcat.jwrap.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by tuke on 2020/7/22
 * <pre>
 *  ----    -----    ------.-----.------     -----.-----.-----
 * scalar  scalar[]       vector/map           vector[]/map[]
 *  ----    -----    ------.-----.------     -----.-----.-----
 *         scalar                           scalar scalar scalar
 * scalar  scalar    scalar scalar scalar   scalar scalar scalar
 *         scalar                           scalar scalar scalar
 *  ----    -----    ------.-----.------     ----.-----.-----
 * </pre>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsCell {

    boolean ignored() default false;

    //  filed index
    int fieldIndex() default -1;

    //  column span
    int span() default 1;

    // expand
    boolean expanded() default false;
}
