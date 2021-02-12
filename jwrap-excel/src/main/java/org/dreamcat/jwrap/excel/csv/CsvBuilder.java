package org.dreamcat.jwrap.excel.csv;

import java.lang.reflect.Field;
import java.util.List;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.jwrap.excel.annotation.CsvFormat;

/**
 * Create by tuke on 2020/8/10
 */
public final class CsvBuilder {

    private CsvBuilder() {
    }

    public static CsvMeta parse(Class<?> clazz) {
        CsvMeta meta = new CsvMeta();

        CsvFormat csvFormat = clazz.getDeclaredAnnotation(CsvFormat.class);
        if (csvFormat != null) {
            try {
                meta.deserializer = csvFormat.typeDeserializer().newInstance();
                meta.serializer = csvFormat.typeSerializer().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }

        List<Field> fields = ReflectUtil.retrieveFields(clazz);
        int index = 0;
        for (Field field : fields) {
            csvFormat = field.getDeclaredAnnotation(CsvFormat.class);
            int i = index;
            int ind;
            if (csvFormat != null && (ind = csvFormat.index()) != -1) {
                i = ind;
            }
            CsvMeta.Cell cell = meta.computeCell(i);
            cell.index = i;
            cell.field = field;

            if (csvFormat == null) {
                continue;
            }

            if (csvFormat.ignored()) {
                meta.getCells().put(index, CsvMeta.IGNORED_CELL);
                continue;
            }

            try {
                cell.serializer = csvFormat.serializer().newInstance();
                cell.deserializer = csvFormat.deserializer().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
            index++;
        }

        return meta;
    }
}
