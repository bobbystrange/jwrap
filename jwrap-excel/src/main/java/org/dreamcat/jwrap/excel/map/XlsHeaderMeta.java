package org.dreamcat.jwrap.excel.map;

import static org.dreamcat.jwrap.excel.util.ExcelBuilder.term;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.common.util.StringUtil;
import org.dreamcat.jwrap.excel.annotation.XlsHeader;
import org.dreamcat.jwrap.excel.annotation.XlsHeader.SubheaderStyle;
import org.dreamcat.jwrap.excel.core.ExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelSheet;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;
import org.dreamcat.jwrap.excel.util.InternalUtil;

/**
 * Create by tuke on 2021/2/22
 */
@SuppressWarnings("rawtypes")
public class XlsHeaderMeta implements IExcelSheet {

    @Getter
    public String name;
    public ExcelStyle defaultStyle;
    public final Map<Integer, Cell> headers = new HashMap<>();
    // true if any cell's subheader is true
    boolean subheader;
    List<IExcelCell> headerCells;
    // transient
    List<Integer> fieldIndexes;

    public synchronized List<Integer> getFieldIndexes() {
        if (fieldIndexes == null) {
            fieldIndexes = headers.keySet().stream()
                    .sorted()
                    .collect(Collectors.toList());
        }
        return fieldIndexes;
    }

    public synchronized List<IExcelCell> getHeaderCells() {
        if (headerCells == null) {
            initHeaderCells();
        }
        return headerCells;
    }

    private void initHeaderCells() {
        headerCells = new ArrayList<>();

        int offset = 0;
        int rowSpan = subheader ? 2 : 1;
        for (int fieldIndex : getFieldIndexes()) {
            Cell cell = headers.get(fieldIndex);

            String header = cell.getHeader();
            ExcelCell excelCell = term(header, 0, offset);
            headerCells.add(excelCell);

            ExcelStyle style = cell.style;
            if (style != null) {
                excelCell.setStyle(style);
            }
            int span = cell.span;
            XlsHeaderMeta expandedMeta = cell.expandedMeta;
            if (!cell.subheader || expandedMeta == null) {
                excelCell.setRowSpan(rowSpan);
                excelCell.setColumnSpan(span);
                offset++;
                continue;
            }
            excelCell.setRowSpan(1);

            List<IExcelCell> subCells = expandedMeta.getHeaderCells();
            for (IExcelCell c : subCells) {
                ExcelCell subCell = (ExcelCell) c;
                subCell.setColumnIndex(offset + subCell.getColumnIndex());

                ExcelStyle subCellStyle = subCell.getStyle();
                if (subCellStyle != null) continue;

                if (cell.subheaderInherited) {
                    subCell.setStyle(style);
                } else {
                    subCell.setStyle(cell.subheaderStyle);
                }
            }
            int width = subCells.size();
            excelCell.setColumnSpan(width * span);
        }
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return getHeaderCells().iterator();
    }

    /**
     * @see XlsHeader
     */
    @Data
    public static class Cell {
        int fieldIndex;
        String fieldName;

        int span = 1;
        boolean expanded;
        XlsHeaderMeta expandedMeta;

        String header;
        ExcelStyle style;
        boolean subheader;
        boolean subheaderInherited;
        ExcelStyle subheaderStyle;

        public String getHeader() {
            if (header == null) {
                header = fieldName;
            }
            return header;
        }

        private Cell fillField(int fieldIndex, String fieldName) {
            this.fieldIndex = fieldIndex;
            // cglib behavior
            if (fieldName.length() == 1) {
                fieldName = StringUtil.toCapitalLowerCase(fieldName);
            }
            this.fieldName = fieldName;
            return this;
        }

        private void fillHeader(XlsHeader xlsHeader, XlsHeaderMeta meta) {
            header = xlsHeader.header();
            subheader = xlsHeader.subheader();
            if (subheader) meta.subheader = true;
            subheaderInherited = xlsHeader.subheaderInherited();

            style = ExcelStyle.from(xlsHeader.style());
            style.setFont(ExcelFont.from(xlsHeader.font()));

            SubheaderStyle subStyle = xlsHeader.subheaderStyle();
            if (subStyle.enabled()) {
                subheaderStyle = ExcelStyle.from(subStyle.style());
                subheaderStyle.setFont(ExcelFont.from(subStyle.font()));
            }
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static XlsHeaderMeta parse(Class<?> clazz) {
        return parse(clazz, true);
    }

    public static XlsHeaderMeta parse(Class<?> clazz, boolean enableExpanded) {
        XlsHeaderMeta meta = new XlsHeaderMeta();
        boolean onlyAnnotated = parseXlsHeaderDefault(meta, clazz);

        List<Field> fields = ReflectUtil.retrieveFields(clazz);
        int index = 0;
        for (Field field : fields) {
            Cell cell = parseXlsHeader(meta, clazz, field, index,
                    onlyAnnotated, enableExpanded);
            if (cell == null) continue;
            index++;
        }

        return meta;
    }

    private static boolean parseXlsHeaderDefault(XlsHeaderMeta meta, Class<?> clazz) {
        XlsHeader.Default xlsHeaderDefault = ReflectUtil.retrieveAnnotation(
                clazz, XlsHeader.Default.class);
        if (xlsHeaderDefault == null) return false;

        ExcelStyle style = ExcelStyle.from(xlsHeaderDefault.style());
        style.setFont(ExcelFont.from(xlsHeaderDefault.font()));
        meta.defaultStyle = style;
        return xlsHeaderDefault.onlyAnnotated();
    }

    private static Cell parseXlsHeader(
            XlsHeaderMeta meta, Class<?> clazz, Field field, int index,
            boolean onlyAnnotated, boolean enableExpanded) {
        XlsHeader xlsHeader = field.getDeclaredAnnotation(XlsHeader.class);
        if (xlsHeader == null) {
            if (onlyAnnotated) return null;
            return meta.headers.computeIfAbsent(index, i -> new Cell())
                    .fillField(index, field.getName());
        }
        if (xlsHeader.ignored()) return null;

        Cell cell = meta.headers.computeIfAbsent(index, i -> new Cell());

        int fieldIndex = xlsHeader.fieldIndex();
        if (fieldIndex == -1) {
            cell.fillField(index, field.getName());
        } else {
            cell.fillField(fieldIndex, field.getName());
        }
        cell.fillHeader(xlsHeader, meta);

        boolean expanded = xlsHeader.expanded();
        cell.setExpanded(expanded);
        if (!enableExpanded || !expanded) return cell;

        Class<?> fieldClass = InternalUtil.getFieldClass(field);
        if (Map.class.isAssignableFrom(fieldClass)) {
            throw new IllegalArgumentException(
                    "@XlsHeader(expanded=true) cannot be applied in class " + fieldClass + " on field " + field);
        }

        if (clazz.equals(fieldClass)) {
            cell.setExpandedMeta(meta);
        } else {
            XlsHeaderMeta fieldMetadata = parse(fieldClass, false);
            cell.setExpandedMeta(fieldMetadata);
        }
        return cell;
    }

}
