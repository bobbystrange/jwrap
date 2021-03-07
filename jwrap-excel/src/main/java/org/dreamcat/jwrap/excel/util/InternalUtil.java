package org.dreamcat.jwrap.excel.util;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.dreamcat.common.util.ReflectUtil;

/**
 * Create by tuke on 2021/2/17
 */
public final class InternalUtil {

    private InternalUtil(){
    }

    public static Font getFont(int fontIndex, Workbook workbook) {
        int fontNum = workbook.getNumberOfFonts();
        if (fontIndex >= 0 && fontIndex < fontNum) {
            return workbook.getFontAt(fontIndex);
        }
        return null;
    }

    public static Class<?> getFieldClass(Field field) {
        Class<?> fieldClass = field.getType();
        if (fieldClass.isAssignableFrom(List.class)) {
            return ReflectUtil.getTypeArgument(field);
        } else if (fieldClass.isArray()) {
            return fieldClass.getComponentType();
        }
        return fieldClass;
    }
}
