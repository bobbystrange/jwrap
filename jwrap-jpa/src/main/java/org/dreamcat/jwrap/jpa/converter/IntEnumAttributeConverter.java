package org.dreamcat.jwrap.jpa.converter;

import java.util.Objects;
import java.util.function.IntSupplier;
import javax.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

/**
 * Create by tuke on 2020/10/19
 */
//@javax.persistence.Converter(autoApply = true)
@RequiredArgsConstructor
public class IntEnumAttributeConverter<E extends Enum<E> & IntSupplier>
        implements AttributeConverter<E, Integer> {

    private final Class<E> clazz;

    @Override
    public Integer convertToDatabaseColumn(E attribute) {
        return attribute == null ? null : attribute.getAsInt();
    }

    @Override
    public E convertToEntityAttribute(Integer dbData) {
        E[] enums = clazz.getEnumConstants();
        for (E e : enums) {
            if (Objects.equals(dbData, e.getAsInt())) {
                return e;
            }
        }
        return null;
    }
}
