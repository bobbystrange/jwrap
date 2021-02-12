package org.dreamcat.jwrap.excel.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.dreamcat.common.io.csv.CsvReader;
import org.dreamcat.common.util.BeanUtil;
import org.dreamcat.jwrap.excel.csv.CsvMeta.Cell;

/**
 * Create by tuke on 2020/7/28
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CsvWorkbook<T> implements ICsvWorkbook {

    private final List<T> values;

    public CsvWorkbook(List<T> values) {
        this.values = values;
    }

    public CsvWorkbook() {
        this(new ArrayList<>());
    }

    public static CsvWorkbook<List<String>> from(String filename) throws IOException {
        return from(filename, null);
    }

    public static <T> CsvWorkbook<T> from(String filename, Class<T> clazz) throws IOException {
        return from(new File(filename), clazz);
    }

    public static CsvWorkbook<List<String>> from(File file) throws IOException {
        return from(file, null);
    }

    public static <T> CsvWorkbook<T> from(File file, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return from(reader, clazz);
        }
    }

    public static CsvWorkbook<List<String>> from(Reader reader) throws IOException {
        return from(reader, null);
    }

    public static <T> CsvWorkbook from(Reader reader, Class<T> clazz) throws IOException {
        if (clazz == null) {
            // nop
            List<List<String>> values = new ArrayList<>();
            try (CsvReader csvReader = new CsvReader(reader)) {
                List<String> record;
                while ((record = csvReader.readRecord()) != null) {
                    values.add(record);
                }
            }
            return new CsvWorkbook<>(values);
        }

        List<T> values = new ArrayList<>();
        CsvWorkbook<T> workbook = new CsvWorkbook<>(values);
        CsvMeta meta = CsvBuilder.parse(clazz);

        try (CsvReader csvReader = new CsvReader(reader)) {
            List<String> record;
            while ((record = csvReader.readRecord()) != null) {
                if (record.isEmpty()) values.add(null);

                T object;
                try {
                    object = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
                values.add(object);

                for (int i = 0, size = record.size(); i < size; i++) {
                    String word = record.get(i);
                    Cell cell = meta.getCells().get(i);
                    if (cell == null || cell.ignored()) continue;

                    Field field = cell.field;
                    Class<?> fieldClass = field.getType();
                    Object fieldValue = null;
                    if (cell.deserializer != null) {
                        fieldValue = cell.deserializer.apply(word);
                    } else if (fieldClass.equals(String.class)) {
                        fieldValue = word;
                    } else if (fieldClass.equals(Integer.class)) {
                        fieldValue = Integer.valueOf(word);
                    } else if (fieldClass.equals(Long.class)) {
                        fieldValue = Long.valueOf(word);
                    } else if (fieldClass.equals(Double.class)) {
                        fieldValue = Double.valueOf(word);
                    } else if (fieldClass.equals(Date.class)) {
                        fieldValue = new Date(Long.parseLong(word));
                    }

                    try {
                        field.setAccessible(true);
                        field.set(object, fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
        }
        return workbook;
    }

    @Override
    public Iterator<Iterable<String>> iterator() {
        return this.new Iter();
    }

    class Iter implements Iterator<Iterable<String>> {

        final Iterator<T> iterator;

        Iter() {
            iterator = values.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Iterable<String> next() {
            T record = iterator.next();
            if (record instanceof List) {
                return ((List<String>) record);
            }

            return BeanUtil.toStringList(record);
        }
    }
}
