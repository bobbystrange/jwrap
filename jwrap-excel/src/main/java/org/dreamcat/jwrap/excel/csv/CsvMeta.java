package org.dreamcat.jwrap.excel.csv;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.Data;

/**
 * Create by tuke on 2020/8/10
 */
@Data
public class CsvMeta {

    // internal use only, for ignored filed
    public static final Cell IGNORED_CELL = new Cell();

    Map<Integer, Cell> cells;
    Function<String[], Object> deserializer;
    Function<Object, String[]> serializer;

    public CsvMeta() {
        this.cells = new HashMap<>();
    }

    public Cell computeCell(int index) {
        Cell cell = cells.computeIfAbsent(index, it -> new Cell());
        if (cell.equals(IGNORED_CELL)) return null;
        return cell;
    }

    @Data
    public static class Cell {

        Field field;
        int index;

        // format
        Function<Object, String> serializer;
        Function<String, Object> deserializer;

        public boolean ignored() {
            return equals(CsvMeta.IGNORED_CELL);
        }
    }
}
