package org.dreamcat.jwrap.excel.parse.tow;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.core.FieldColumn;
import org.dreamcat.common.util.ArrayUtil;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.jwrap.excel.parse.IExcelParser;
import org.dreamcat.jwrap.excel.parse.XlsParse;

/**
 * Create by tuke on 2020/8/27
 */
@Slf4j
@Setter
public class SVParser<S, V> implements
        IExcelParser<org.dreamcat.jwrap.excel.parse.tow.SVRow<S, V>> {

    private int headerIndex = 0;
    // tow level sequence column index
    private final int scalarSequenceIndex;
    // vector locator, priority: scalarMapSize > vectorFirstHeaderName > vectorFirstHeaderRegexp
    private int scalarMapSize = -1;
    private String vectorFirstHeaderName;
    private String vectorFirstHeaderRegexp;

    private final Class<S> scalarClass;
    private final Class<V> vectorClass;
    private final List<FieldColumn> scalarColumns;
    private final List<FieldColumn> vectorColumns;

    final Field scalarField;
    final Field scalarMapField;
    final Field vectorField;

    final Map<Field, Function<String, Object>> deserializerMap;

    public SVParser(Class<S> scalarClass, Class<V> vectorClass, int scalarSequenceIndex) {
        this.scalarSequenceIndex = scalarSequenceIndex;
        this.scalarClass = scalarClass;
        this.vectorClass = vectorClass;
        this.scalarColumns = FieldColumn.parse(scalarClass);
        this.vectorColumns = FieldColumn.parse(vectorClass);

        Map<String, Field> fieldMap = ReflectUtil.retrieveFieldMap(
                org.dreamcat.jwrap.excel.parse.tow.SVRow.class);
        this.scalarField = fieldMap.get("scalar");
        this.scalarMapField = fieldMap.get("map");
        this.vectorField = fieldMap.get("vector");

        this.deserializerMap = new HashMap<>();

        retainXlsParse(scalarColumns);
        retainXlsParse(vectorColumns);
    }

    @Override
    public List<org.dreamcat.jwrap.excel.parse.tow.SVRow<S, V>> readSheetAsValue(List<List<String>> sheet) throws Exception {
        if (ObjectUtil.isEmpty(sheet)) return null;

        int rowSize = sheet.size();
        if (headerIndex >= rowSize) {
            throw new IllegalArgumentException(
                    "headerIndex >= rowSize (last row num in sheet) in this sheet");
        }

        List<String> headers = sheet.get(headerIndex);
        if (ObjectUtil.isEmpty(headers)) {
            throw new IllegalArgumentException(
                    "empty header row in row " + headerIndex);
        }

        List<org.dreamcat.jwrap.excel.parse.tow.SVRow<S, V>> list = new ArrayList<>();
        next:
        for (int i = headerIndex + 1; i < rowSize; i++) {
            List<String> values = sheet.get(i);
            if (ObjectUtil.isEmpty(values)) {
                log.warn("empty row {}, ignore it", i);
                continue;
            }

            values:
            for (; ; ) {
                if (ObjectUtil.isEmpty(values)) {
                    if (log.isDebugEnabled()) {
                        log.warn("empty content in row {}, ignore it", i);
                    }
                    continue next;
                }

                int columnSize = values.size();
                if (scalarSequenceIndex >= columnSize) {
                    throw new IllegalArgumentException(
                            "sequenceIndex >= columnSize (last column num in row) in this sheet");
                }

                Object sequence = values.get(scalarSequenceIndex);
                if (sequence == null) {
                    throw new IllegalArgumentException("missing sequence in row " + i);
                }

                org.dreamcat.jwrap.excel.parse.tow.SVRow<S, V> bean = new org.dreamcat.jwrap.excel.parse.tow.SVRow<>();
                int width = readOneRow(bean, headers, values);
                list.add(bean);
                while (i < rowSize - 1) {
                    i++;
                    values = sheet.get(i);
                    columnSize = values.size();
                    if (scalarSequenceIndex >= columnSize) {
                        throw new IllegalArgumentException(
                                "sequenceIndex >= columnSize (last column num in row) in this sheet");
                    }
                    sequence = values.get(scalarSequenceIndex);
                    if (sequence != null) continue values;

                    org.dreamcat.jwrap.excel.parse.tow.SVColumn<V> svColumn = readSVColumn(headers, values, width);
                    bean.getVector().add(svColumn);
                }
                break;
            }
        }

        return list;
    }

    private int readOneRow(org.dreamcat.jwrap.excel.parse.tow.SVRow<S, V> bean, List<String> headers, List<String> values)
            throws IllegalAccessException {
        S scalar = ReflectUtil.newInstance(scalarClass);
        scalarField.set(bean, scalar);

        int offset = 0;
        for (FieldColumn column : scalarColumns) {
            Field field = column.getField();
            List<FieldColumn> children = column.getChildren();
            List<Annotation> annotations = column.getAnnotations();
            // single field, then just set it
            if (children == null) {
                String value = ArrayUtil.get(values, offset++);
                if (value == null) continue;

                Function<String, Object> deserializer = deserializerMap.get(field);
                if (deserializer == null) {
                    if (ObjectUtil.isEmpty(annotations)) {
                        field.set(scalar, ReflectUtil.parse(value, field.getType()));
                        continue;
                    } else {
                        org.dreamcat.jwrap.excel.parse.XlsParse xlsParse = (org.dreamcat.jwrap.excel.parse.XlsParse) annotations.get(0);
                        Class<? extends Function<String, Object>> deserializerClass = xlsParse
                                .deserializer();
                        deserializer = ReflectUtil.newInstance(deserializerClass);
                        deserializerMap.put(field, deserializer);
                    }
                }

                field.set(scalar, deserializer.apply(value));
                continue;
            }

            Object fieldObject = ReflectUtil.newInstance(field.getType());
            field.set(scalar, fieldObject);
            for (FieldColumn child : children) {
                Field childField = child.getField();
                List<Annotation> childAnnotations = child.getAnnotations();
                String value = ArrayUtil.get(values, offset++);
                if (value == null) continue;

                Function<String, Object> deserializer = deserializerMap.get(childField);
                if (deserializer == null) {
                    if (ObjectUtil.isEmpty(childAnnotations)) {
                        childField.set(fieldObject, ReflectUtil.parse(value, field.getType()));
                        continue;
                    } else {
                        org.dreamcat.jwrap.excel.parse.XlsParse xlsParse = (org.dreamcat.jwrap.excel.parse.XlsParse) childAnnotations.get(0);
                        Class<? extends Function<String, Object>> deserializerClass = xlsParse
                                .deserializer();
                        deserializer = ReflectUtil.newInstance(deserializerClass);
                        deserializerMap.put(field, deserializer);
                    }
                }
                childField.set(fieldObject, deserializer.apply(value));
            }
        }

        if (scalarMapSize != -1) {
            ObjectUtil.requireNotNegative(scalarMapSize, "scalarMapSize");
            offset += scalarMapSize;
        } else if (vectorFirstHeaderName != null) {
            int k = offset;
            int size = headers.size();
            for (; k < size; k++) {
                String header = ArrayUtil.get(headers, k);
                if (vectorFirstHeaderName.equals(header)) break;
            }
            // vector not found
            if (k == size) return -1;

            fillScalarMap(bean, headers, values, offset, k);
            offset = k;
        } else if (vectorFirstHeaderRegexp != null) {
            int k = offset;
            int size = headers.size();
            for (; k < size; k++) {
                String header = ArrayUtil.get(headers, k);
                if (header == null || header.matches(vectorFirstHeaderRegexp)) break;
            }
            // vector not found
            if (k == size) return -1;
            fillScalarMap(bean, headers, values, offset, k);
            offset = k;
        }

        int width = offset;
        // vector
        List<org.dreamcat.jwrap.excel.parse.tow.SVColumn<V>> vector = new ArrayList<>();
        vectorField.set(bean, vector);
        org.dreamcat.jwrap.excel.parse.tow.SVColumn<V> svColumn = readSVColumn(headers, values, offset);
        vector.add(svColumn);
        return width;
    }

    private org.dreamcat.jwrap.excel.parse.tow.SVColumn<V> readSVColumn(List<String> headers, List<String> values, int offset)
            throws IllegalAccessException {
        org.dreamcat.jwrap.excel.parse.tow.SVColumn<V> svColumn = new org.dreamcat.jwrap.excel.parse.tow.SVColumn<>();

        V svColumnScalar = ReflectUtil.newInstance(vectorClass);
        scalarField.set(svColumn, svColumnScalar);
        for (FieldColumn column : vectorColumns) {
            Field field = column.getField();
            List<FieldColumn> children = column.getChildren();
            List<Annotation> annotations = column.getAnnotations();
            // single field, then just set it
            if (children == null) {
                String value = ArrayUtil.get(values, offset++);
                if (value == null) continue;

                Function<String, Object> deserializer = deserializerMap.get(field);
                if (deserializer == null) {
                    if (ObjectUtil.isEmpty(annotations)) {
                        field.set(svColumnScalar, ReflectUtil.parse(value, field.getType()));
                        continue;
                    } else {
                        org.dreamcat.jwrap.excel.parse.XlsParse xlsParse = (org.dreamcat.jwrap.excel.parse.XlsParse) annotations.get(0);
                        Class<? extends Function<String, Object>> deserializerClass = xlsParse
                                .deserializer();
                        deserializer = ReflectUtil.newInstance(deserializerClass);
                        deserializerMap.put(field, deserializer);
                    }
                }

                field.set(svColumnScalar, deserializer.apply(value));
                continue;
            }

            Object fieldObject = ReflectUtil.newInstance(field.getType());
            field.set(svColumnScalar, fieldObject);
            for (FieldColumn child : children) {
                Field childField = child.getField();
                List<Annotation> childAnnotations = child.getAnnotations();
                String value = ArrayUtil.get(values, offset++);
                if (value == null) continue;

                Function<String, Object> deserializer = deserializerMap.get(childField);
                if (deserializer == null) {
                    if (ObjectUtil.isEmpty(childAnnotations)) {
                        childField.set(fieldObject, ReflectUtil.parse(value, field.getType()));
                        continue;
                    } else {
                        org.dreamcat.jwrap.excel.parse.XlsParse xlsParse = (org.dreamcat.jwrap.excel.parse.XlsParse) childAnnotations.get(0);
                        Class<? extends Function<String, Object>> deserializerClass = xlsParse
                                .deserializer();
                        deserializer = ReflectUtil.newInstance(deserializerClass);
                        deserializerMap.put(field, deserializer);
                    }
                }
                childField.set(fieldObject, deserializer.apply(value));
            }
        }

        int size = headers.size();
        if (offset < size) {
            fillVectorMap(svColumn, headers, values, offset, size);
        }
        return svColumn;
    }

    private void fillScalarMap(SVRow<S, V> bean, List<String> headers, List<String> values,
            int start, int end) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        scalarMapField.set(bean, map);
        for (int i = start; i < end; i++) {
            map.put(headers.get(i), ArrayUtil.get(values, i));
        }
    }

    private void fillVectorMap(SVColumn<V> svColumn, List<String> headers, List<String> values,
            int start, int end) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        scalarMapField.set(svColumn, map);
        for (int i = start; i < end; i++) {
            map.put(headers.get(i), ArrayUtil.get(values, i));
        }
    }

    private static void retainXlsParse(List<FieldColumn> columns) {
        for (FieldColumn column : columns) {
            Field field = column.getField();
            field.setAccessible(true);

            List<Annotation> annotations = column.getAnnotations();
            if (ObjectUtil.isEmpty(annotations)) continue;
            annotations.removeIf(annotation -> !(annotation instanceof org.dreamcat.jwrap.excel.parse.XlsParse));

            List<FieldColumn> children = column.getChildren();
            if (ObjectUtil.isEmpty(children)) continue;

            // only recurse 2-level
            for (FieldColumn child : children) {
                Field childField = child.getField();
                childField.setAccessible(true);

                List<Annotation> childAnnotations = child.getAnnotations();
                if (ObjectUtil.isEmpty(childAnnotations)) continue;
                childAnnotations.removeIf(annotation -> !(annotation instanceof XlsParse));
            }
        }
    }

}
