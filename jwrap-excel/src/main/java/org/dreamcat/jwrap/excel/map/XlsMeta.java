package org.dreamcat.jwrap.excel.map;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.common.util.StringUtil;
import org.dreamcat.common.x.asm.BeanMapUtil;
import org.dreamcat.jwrap.excel.annotation.XlsCell;
import org.dreamcat.jwrap.excel.annotation.XlsFont;
import org.dreamcat.jwrap.excel.annotation.XlsFormat;
import org.dreamcat.jwrap.excel.annotation.XlsRichStyle;
import org.dreamcat.jwrap.excel.annotation.XlsSheet;
import org.dreamcat.jwrap.excel.annotation.XlsStyle;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/24
 */
@SuppressWarnings("rawtypes")
public class XlsMeta {

    // @XlsSheet
    public String name;
    public org.dreamcat.jwrap.excel.style.ExcelFont defaultFont;
    public org.dreamcat.jwrap.excel.style.ExcelStyle defaultStyle;
    public final Map<Integer, Cell> cells;

    List<Integer> fieldIndexes;

    public XlsMeta() {
        this.cells = new HashMap<>();
    }

    public List getFieldValues(Object row) {
        Map<String, Object> fields = BeanMapUtil.toMap(row);
        List<Object> fieldValues = new ArrayList<>(fields.size());

        Collection<Cell> c = cells.values();
        for (XlsMeta.Cell cell : c) {
            fieldValues.add(fields.get(cell.fieldName));
        }
        return fieldValues;
    }

    public synchronized List<Integer> getFieldIndexes() {
        if (fieldIndexes == null) {
            fieldIndexes = cells.keySet().stream()
                    .sorted()
                    .collect(Collectors.toList());
        }
        return fieldIndexes;
    }

    // @XlsCell
    @Data
    public static class Cell {

        int fieldIndex;
        String fieldName;

        int span = 1;
        boolean expanded;

        org.dreamcat.jwrap.excel.style.ExcelFont font;
        org.dreamcat.jwrap.excel.style.ExcelStyle style;
        // only not null if expended on a array field
        Class<?> expandedType;
        XlsMeta expandedMeta;

        // format
        Function serializer;
        Function deserializer;

        public Cell fillField(int fieldIndex, String fieldName) {
            this.fieldIndex = fieldIndex;
            // cglib behavior
            if (fieldName.length() == 1) {
                fieldName = StringUtil.toCapitalLowerCase(fieldName);
            }
            this.fieldName = fieldName;
            return this;
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static XlsMeta parse(Class<?> clazz) {
        return parse(clazz, true);
    }

    public static XlsMeta parse(Class<?> clazz, boolean enableExpanded) {
        XlsMeta meta = new XlsMeta();
        Boolean onlyAnnotated = parseXlsSheet(meta, clazz);
        if (onlyAnnotated == null) return null;

        parseXlsFont(meta, clazz);
        parseXlsStyle(meta, clazz);

        List<Field> fields = ReflectUtil.retrieveFields(clazz);
        int index = 0;
        for (Field field : fields) {
            Cell cell = parseXlsCell(meta, clazz, field, index, onlyAnnotated, enableExpanded);
            if (cell == null) continue;

            parseXlsFont(cell, field);
            parseXlsStyle(cell, field);
            parseXlsFormat(cell, field);
            index++;
        }

        return meta;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    // true or false to determine whether only annotated fields are processed, null to skip the parsing process
    private static Boolean parseXlsSheet(XlsMeta meta, Class<?> clazz) {
        XlsSheet xlsSheet = ReflectUtil.retrieveAnnotation(clazz, XlsSheet.class);
        if (xlsSheet == null) return null;

        meta.name = xlsSheet.name();
        return xlsSheet.onlyAnnotated();
    }

    private static void parseXlsFont(XlsMeta meta, Class<?> clazz) {
        XlsFont xlsFont = ReflectUtil.retrieveAnnotation(clazz, XlsFont.class);
        if (xlsFont == null) return;
        meta.defaultFont = org.dreamcat.jwrap.excel.style.ExcelFont.from(xlsFont);
    }

    private static void parseXlsStyle(XlsMeta meta, Class<?> clazz) {
        XlsStyle xlsStyle = ReflectUtil.retrieveAnnotation(clazz, XlsStyle.class);
        if (xlsStyle == null) return;
        XlsRichStyle xlsRichStyle = ReflectUtil.retrieveAnnotation(clazz, XlsRichStyle.class);
        meta.defaultStyle = org.dreamcat.jwrap.excel.style.ExcelStyle.from(xlsStyle, xlsRichStyle);
    }

    private static void parseXlsFont(Cell cell, Field field) {
        XlsFont xlsFont = field.getDeclaredAnnotation(XlsFont.class);
        if (xlsFont == null) return;

        org.dreamcat.jwrap.excel.style.ExcelFont font = ExcelFont.from(xlsFont);
        cell.setFont(font);
    }

    private static void parseXlsStyle(Cell cell, Field field) {
        XlsStyle xlsStyle = field.getDeclaredAnnotation(XlsStyle.class);
        XlsRichStyle xlsRichStyle = field.getDeclaredAnnotation(XlsRichStyle.class);
        if (xlsStyle == null && xlsRichStyle == null) return;

        org.dreamcat.jwrap.excel.style.ExcelStyle style = ExcelStyle.from(xlsStyle, xlsRichStyle);
        cell.setStyle(style);
    }

    private static void parseXlsFormat(Cell cell, Field field) {
        XlsFormat xlsFormat = field.getDeclaredAnnotation(XlsFormat.class);
        if (xlsFormat == null) return;

        Class serializer = xlsFormat.serializer();
        Class deserializer = xlsFormat.deserializer();
        if (serializer != XlsFormat.None.class) {
            try {
                cell.serializer = (Function) serializer.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (deserializer != XlsFormat.None.class) {
            try {
                cell.deserializer = (Function) deserializer.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private static Cell parseXlsCell(XlsMeta meta, Class<?> clazz, Field field, int index,
            boolean onlyAnnotated, boolean enableExpanded) {
        XlsCell xlsCell = field.getDeclaredAnnotation(XlsCell.class);
        if (xlsCell == null) {
            if (onlyAnnotated) return null;
            return meta.cells.computeIfAbsent(index, i -> new Cell())
                    .fillField(index, field.getName());
        }

        if (xlsCell.ignored()) {
            return null;
        }

        Cell cell = meta.cells.computeIfAbsent(index, i -> new Cell());

        int fieldIndex = xlsCell.fieldIndex();
        if (fieldIndex == -1) {
            cell.fillField(index, field.getName());
        } else {
            cell.fillField(fieldIndex, field.getName());
        }
        cell.setSpan(xlsCell.span());
        cell.setExpanded(xlsCell.expanded());

        if (!enableExpanded || !xlsCell.expanded()) return cell;

        Class<?> fieldClass = getFieldClass(field, xlsCell.expandedType());
        cell.setExpandedType(fieldClass);
        if (clazz.equals(fieldClass)) {
            cell.setExpandedMeta(meta);
        } else {
            XlsMeta fieldMetadata = parse(fieldClass, false);
            if (fieldMetadata == null) {
                throw new IllegalArgumentException(
                        "no @XlsSheet in class " + fieldClass + " on field " + field);
            }
            cell.setExpandedMeta(fieldMetadata);
        }
        return cell;
    }

    private static Class<?> getFieldClass(Field field, Class expandedType) {
        Class<?> fieldClass = field.getType();
        if (fieldClass.isAssignableFrom(List.class)) {
            if (expandedType == Void.class) {
                throw new IllegalArgumentException(
                        "require to specify XlsCell#expandedType in List filed on field " + field);
            }
            fieldClass = expandedType;
        } else if (fieldClass.isArray()) {
            fieldClass = fieldClass.getComponentType();
        }
        return fieldClass;
    }

}
