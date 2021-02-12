package org.dreamcat.jwrap.jpa.converter;

import javax.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.dreamcat.common.x.jackson.JacksonUtil;

/**
 * Create by tuke on 2020/9/17
 */
@RequiredArgsConstructor
public class JsonAttributeConverter<T> implements AttributeConverter<T, String> {

    private final Class<T> clazz;

    @Override
    public String convertToDatabaseColumn(T attribute) {
        return JacksonUtil.toJson(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        return JacksonUtil.fromJson(dbData, clazz);
    }
}
