package org.dreamcat.jwrap.excel.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.jwrap.excel.content.ExcelUnionContent;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelSheet;
import org.dreamcat.jwrap.excel.core.IExcelWorkbook;
import org.dreamcat.jwrap.excel.map.XlsMeta.Cell;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/25
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationRowSheet implements IExcelSheet {

    private final Map<Class, MetaCacheLine> metaMap = new HashMap<>();
    @Setter
    private String name;
    @Setter
    private IExcelWorkbook<?> workbook;
    private Object scheme;
    private XlsMeta meta;
    private List<Integer> indexes;

    public AnnotationRowSheet(Object scheme) {
        this(scheme, null);
    }

    public AnnotationRowSheet(Object scheme, IExcelWorkbook<?> workbook) {
        this.workbook = workbook;
        reset(scheme);
    }

    private static boolean isNotListOrArray(Object o) {
        return !(o instanceof List) && !(o.getClass().isArray());
    }

    public void reset(Object scheme) {
        Class clazz = scheme.getClass();
        if (metaMap.containsKey(clazz)) return;

        this.meta = XlsMeta.parse(clazz);
        checkMetaName(clazz);
        this.indexes = meta.getFieldIndexes();
        this.metaMap.put(clazz, new MetaCacheLine(meta, indexes));
        if (workbook != null) {
            workbook.registerFonts(meta.getFonts()).
                    registerStyles(meta.getStyles());
        }

        this.name = meta.name;
        this.scheme = scheme;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }
    /// static area

    private void checkMetaName(Class clazz) {
        if (ObjectUtil.isEmpty(meta.name)) {
            throw new IllegalArgumentException(
                    "sheet name is empty in " + clazz + ", check its annotations");
        }
    }

    @AllArgsConstructor
    static class MetaCacheLine {

        XlsMeta meta;
        List<Integer> indexes;
    }

    @Getter
    class Iter extends ExcelUnionContent implements Iterator<IExcelCell>, IExcelCell {

        XlsMeta subMeta;
        List<Integer> subIndexes;

        List row;
        int schemeSize;
        int schemeIndex;
        int maxRowSpan;
        int offset;

        Object scalar;

        List scalarArray;
        int scalarArraySize;
        int scalarArrayIndex;

        List vector;
        int vectorSize;
        int vectorIndex;

        List<List> vectorArray;
        int vectorArraySize;
        int vectorArrayIndex;
        int vectorArrayColumnSize;
        int vectorArrayColumnIndex;

        List dynamic;
        int dynamicSize;
        int dynamicIndex;

        List<List> dynamicArray;
        int dynamicArraySize;
        int dynamicArrayIndex;
        int dynamicArrayColumnSize;
        int dynamicArrayColumnIndex;

        int rowIndex;
        int columnIndex;
        int rowSpan;
        int columnSpan;
        ExcelFont font;
        ExcelStyle style;

        Iter() {
            init();
        }

        public void reset(Object scheme) {
            AnnotationRowSheet.this.reset(scheme);

            subMeta = null;
            subIndexes = null;

            scalar = null;

            scalarArray = null;
            scalarArraySize = 0;
            scalarArrayIndex = 0;

            vector = null;
            vectorSize = 0;
            vectorIndex = 0;

            vectorArray = null;
            vectorArraySize = 0;
            vectorArrayIndex = 0;
            vectorArrayColumnSize = 0;
            vectorArrayColumnIndex = 0;

            dynamic = null;
            dynamicSize = 0;
            dynamicIndex = 0;

            dynamicArray = null;
            dynamicArraySize = 0;
            dynamicArrayIndex = 0;
            dynamicArrayColumnSize = 0;
            dynamicArrayColumnIndex = 0;

            init();
        }

        private void init() {
            row = meta.getFieldValues(scheme);
            maxRowSpan = 1;
            offset = 0;
            for (Object fieldValue : row) {
                if (fieldValue instanceof List) {
                    maxRowSpan = Math.max(maxRowSpan, ((List) fieldValue).size());
                } else if (fieldValue instanceof Object[]) {
                    maxRowSpan = Math.max(maxRowSpan, ((Object[]) fieldValue).length);
                }
            }

            schemeSize = row.size();
            schemeIndex = 0;
            if (schemeSize > 0) {
                move();
            }
        }

        @Override
        public IExcelContent getContent() {
            return this;
        }

        @Override
        public int getFontIndex() {
            return font != null ? font.getIndex() : -1;
        }

        @Override
        public int getStyleIndex() {
            return style != null ? style.getIndex() : -1;
        }

        @Override
        public boolean hasNext() {
            // empty scheme
            if (schemeSize == 0) return false;
            // reach all schemes
            if (schemeIndex >= schemeSize) return false;
            // has cells
            return scalar != null ||
                    scalarArray != null ||
                    vector != null ||
                    vectorArray != null ||
                    dynamic != null ||
                    dynamicArray != null;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            Cell cell = meta.cells.get(indexes.get(schemeIndex));

            if (scalar != null) {
                // prepare data
                if (cell.serializer == null) {
                    setContent(scalar);
                } else {
                    setContent(cell.serializer.apply(scalar));
                }
                rowIndex = 0;
                columnIndex = offset;
                rowSpan = maxRowSpan;
                columnSpan = cell.span;
                fillFontAndStyle(cell);

                // move
                scalar = null;
                offset += columnSpan;
                schemeIndex++;
                if (schemeIndex < schemeSize) {
                    move();
                }
                return this;
            }

            // in cell case scheme
            if (scalarArray != null) {
                if (cell.serializer == null) {
                    setContent(scalarArray.get(scalarArrayIndex));
                } else {
                    setContent(cell.serializer.apply(scalarArray.get(scalarArrayIndex)));
                }
                rowIndex = scalarArrayIndex;
                columnIndex = offset;
                rowSpan = 1;
                columnSpan = cell.span;
                fillFontAndStyle(cell);

                // move
                scalarArrayIndex++;
                if (scalarArrayIndex >= scalarArraySize) {
                    offset += columnSpan;
                    scalarArray = null;
                    schemeIndex++;
                    if (schemeIndex < schemeSize) {
                        move();
                    }
                }

                return this;
            }

            if (vector != null) {
                Cell subCell = subMeta.cells.get(subIndexes.get(vectorIndex));

                if (cell.serializer == null) {
                    setContent(vector.get(vectorIndex));
                } else {
                    setContent(cell.serializer.apply(vector.get(vectorIndex)));
                }
                rowIndex = 0;
                columnIndex = offset++;
                rowSpan = maxRowSpan;
                columnSpan = subCell.span;
                fillFontAndStyle(subCell, cell);

                // move
                vectorIndex++;
                if (vectorIndex >= vectorSize) {
                    vector = null;
                    schemeIndex++;
                    if (schemeIndex < schemeSize) {
                        move();
                    }
                }
                return this;
            }

            if (dynamic != null) {
                if (cell.serializer == null) {
                    setContent(dynamic.get(dynamicIndex));
                } else {
                    setContent(cell.serializer.apply(dynamic.get(dynamicIndex)));
                }
                rowIndex = 0;
                columnIndex = offset++;
                rowSpan = maxRowSpan;
                columnSpan = cell.span;
                fillFontAndStyle(cell, cell);

                // move
                dynamicIndex++;
                if (dynamicIndex >= dynamicSize) {
                    dynamic = null;
                    schemeIndex++;
                    if (schemeIndex < schemeSize) {
                        move();
                    }
                }
                return this;
            }

            if (dynamicArray != null) {
                if (cell.serializer == null) {
                    setContent(dynamicArray.get(dynamicArrayIndex).get(dynamicArrayColumnIndex));
                } else {
                    setContent(cell.serializer.apply(dynamicArray.get(dynamicArrayIndex)
                            .get(dynamicArrayColumnIndex)));
                }
                rowIndex = dynamicArrayIndex;
                columnIndex = offset + dynamicArrayColumnIndex;
                rowSpan = 1;
                columnSpan = cell.span;
                fillFontAndStyle(cell, cell);

                // move
                dynamicArrayColumnIndex++;
                if (dynamicArrayColumnIndex >= dynamicArrayColumnSize) {
                    dynamicArrayColumnIndex = 0;
                    dynamicArrayIndex++;
                    if (dynamicArrayIndex >= dynamicArraySize) {
                        dynamicArray = null;
                        offset = columnIndex + columnSpan;
                        schemeIndex++;
                        if (schemeIndex < schemeSize) {
                            move();
                        }
                    } else {
                        dynamicArrayColumnSize = dynamicArray.get(dynamicArrayIndex).size();
                    }
                }

                return this;
            }

            Cell subCell = subMeta.cells.get(indexes.get(vectorArrayColumnIndex));
            if (cell.serializer == null) {
                setContent(vectorArray.get(vectorArrayIndex).get(vectorArrayColumnIndex));
            } else {
                setContent(cell.serializer
                        .apply(vectorArray.get(vectorArrayIndex).get(vectorArrayColumnIndex)));
            }
            rowIndex = vectorArrayIndex;
            columnIndex = offset + vectorArrayColumnIndex;
            rowSpan = 1;
            columnSpan = subCell.span;
            fillFontAndStyle(subCell, cell);

            // move
            vectorArrayColumnIndex++;
            if (vectorArrayColumnIndex >= vectorArrayColumnSize) {
                vectorArrayColumnIndex = 0;
                vectorArrayIndex++;
                if (vectorArrayIndex >= vectorArraySize) {
                    vectorArray = null;
                    offset = columnIndex + columnSpan;
                    schemeIndex++;
                    if (schemeIndex < schemeSize) {
                        move();
                    }
                } else {
                    vectorArrayColumnSize = vectorArray.get(vectorArrayIndex).size();
                }
            }

            return this;
        }

        // move magical cursor for cells
        private void move() {
            Object fieldValue = row.get(schemeIndex);
            Cell cell = meta.cells.get(indexes.get(schemeIndex));

            if (!cell.expanded) {
                // s
                if (isNotListOrArray(fieldValue)) {
                    if (fieldValue instanceof Map) {
                        Map map = (Map) fieldValue;
                        int mapSize = map.size();

                        if (mapSize == 0) {
                            throw new IllegalArgumentException(
                                    "empty map field value in " + scheme.getClass());
                        }
                        dynamic = new ArrayList(map.values());

                        dynamicSize = mapSize;
                        dynamicIndex = 0;
                        return;
                    }
                    scalar = fieldValue;
                    return;
                }

                List array = ReflectUtil.castToList(fieldValue);
                int arraySize = array.size();
                if (arraySize == 0) {
                    throw new IllegalArgumentException(
                            "empty list/array field value in " + scheme.getClass());
                }

                if (array.get(0) instanceof Map) {
                    List<Map> mapList = (List<Map>) array;

                    dynamicArraySize = arraySize;
                    dynamicArrayIndex = 0;

                    Map dynamicArrayFirstMap = mapList.get(0);
                    int mapSize = dynamicArrayFirstMap.size();
                    if (mapSize == 0) {
                        throw new IllegalArgumentException(
                                "empty map in list/array field value on " + scheme.getClass());
                    }

                    dynamicArray = new ArrayList<>(mapSize);
                    for (Map map : mapList) {
                        dynamicArray.add(new ArrayList<>(map.values()));
                    }
                    dynamicArrayColumnSize = mapSize;
                    dynamicArrayColumnIndex = 0;
                    return;
                }

                // sa
                scalarArray = array;
                scalarArraySize = arraySize;
                scalarArrayIndex = 0;
                return;
            }

            // v
            if (isNotListOrArray(fieldValue)) {
                AnnotationRowSheet.MetaCacheLine cacheLine = metaMap.computeIfAbsent(
                        fieldValue.getClass(), c -> {
                            XlsMeta newMeta = XlsMeta.parse(c, false);
                            if (newMeta == null) {
                                throw new IllegalArgumentException(
                                        "no @XlsSheet annotation on " + c);
                            }
                            return new AnnotationRowSheet.MetaCacheLine(newMeta,
                                    newMeta.getFieldIndexes());
                        });
                subMeta = cacheLine.meta;
                subIndexes = cacheLine.indexes;
                vector = subMeta.getFieldValues(fieldValue);
                vectorSize = vector.size();
                vectorIndex = 0;
                return;
            }

            // va
            List rectangle = ReflectUtil.castToList(fieldValue);
            if (rectangle.isEmpty()) {
                throw new IllegalArgumentException(
                        "empty list/array field value in " + scheme.getClass());
            }
            AnnotationRowSheet.MetaCacheLine cacheLine = metaMap.computeIfAbsent(
                    rectangle.get(0).getClass(), c -> {
                        XlsMeta newMeta = XlsMeta.parse(c, false);
                        if (newMeta == null) {
                            throw new IllegalArgumentException("no @XlsSheet annotation on " + c);
                        }
                        return new AnnotationRowSheet.MetaCacheLine(newMeta,
                                newMeta.getFieldIndexes());
                    });
            subMeta = cacheLine.meta;
            subIndexes = cacheLine.indexes;

            vectorArray = (List<List>) rectangle.stream().map(subMeta::getFieldValues)
                    .collect(Collectors.toList());
            vectorArraySize = vectorArray.size();
            if (vectorArraySize == 0) {
                throw new IllegalArgumentException("empty size element on " + scheme.getClass());
            }
            vectorArrayIndex = 0;
            vectorArrayColumnSize = vectorArray.get(0).size();
            vectorArrayColumnIndex = 0;
        }

        private void fillFontAndStyle(Cell cell) {
            font = cell.font != null ? cell.font : meta.defaultFont;
            style = cell.style != null ? cell.style : meta.defaultStyle;
        }

        private void fillFontAndStyle(Cell subCell, Cell cell) {
            if (subCell.font != null) {
                font = subCell.font;
            } else if (subMeta != null && subMeta.defaultFont != null) {
                font = subMeta.defaultFont;
            } else if (cell.font != null) {
                font = cell.font;
            } else {
                font = meta.defaultFont;
            }

            if (subCell.style != null) {
                style = subCell.style;
            } else if (subMeta != null && subMeta.defaultStyle != null) {
                style = subMeta.defaultStyle;
            } else if (cell.style != null) {
                style = cell.style;
            } else {
                style = meta.defaultStyle;
            }
        }
    }
}
