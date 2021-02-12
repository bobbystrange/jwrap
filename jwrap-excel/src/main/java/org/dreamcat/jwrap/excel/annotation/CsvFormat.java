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
public @interface CsvFormat {

    boolean ignored() default false;

    int index() default -1;

    Class<? extends Function<Object, String>> serializer() default NoneSerializer.class;

    Class<? extends Function<String, Object>> deserializer() default NoneDeserializer.class;

    Class<? extends Function<String[], Object>> typeDeserializer() default NoneTypeDeserializer.class;

    Class<? extends Function<Object, String[]>> typeSerializer() default NoneTypeSerializer.class;

    class NoneSerializer implements Function<Object, String> {

        @Override
        public String apply(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    class NoneDeserializer implements Function<String, Object> {

        @Override
        public Object apply(String o) {
            throw new UnsupportedOperationException();
        }
    }

    class NoneTypeSerializer implements Function<Object, String[]> {

        @Override
        public String[] apply(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    class NoneTypeDeserializer implements Function<String[], Object> {

        @Override
        public Object apply(String[] o) {
            throw new UnsupportedOperationException();
        }
    }
}
