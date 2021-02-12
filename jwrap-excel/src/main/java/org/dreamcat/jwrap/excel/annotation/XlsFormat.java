package org.dreamcat.jwrap.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Create by tuke on 2020/8/10
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SuppressWarnings("rawtypes")
public @interface XlsFormat {

    Class<? extends Function> serializer() default None.class;

    Class<? extends Function> deserializer() default None.class;

    class None implements Function {

        @Override
        public Object apply(Object o) {
            throw new IllegalStateException("this method may not be invoked");
        }
    }
}
