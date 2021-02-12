package org.dreamcat.jwrap.jpa.converter;

import java.util.Objects;
import java.util.function.Supplier;
import javax.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

/**
 * Create by tuke on 2019-05-16
 */
//@javax.persistence.Converter(autoApply = true)
@RequiredArgsConstructor
public class EnumAttributeConverter<E extends Enum<E> & Supplier<T>, T>
        implements AttributeConverter<E, T> {

    private final Class<E> clazz;

    @Override
    public T convertToDatabaseColumn(E attribute) {
        return attribute == null ? null : attribute.get();
    }

    @Override
    public E convertToEntityAttribute(T dbData) {
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (Objects.equals(dbData, e.get())) {
                return e;
            }
        }
        return null;
    }
}
