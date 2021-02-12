package org.dreamcat.jwrap.excel.map.serializable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelSheet;

/**
 * Create by tuke on 2021/2/4
 */
@Data
public class SerializableSheet implements IExcelSheet, Serializable {

    private String name;
    private List<SerializablePiece> pieces;

    // with schema
    public static <T> SerializableSheet from(Class<?> headClass, List<T> body) {

        return null;
    }

    // without schema
    public static <T> SerializableSheet from(
            List<SerializablePieceSimpleHead> head, List<Map<String, Object>> body) {

        return null;
    }

    @Override
    public Iterator<org.dreamcat.jwrap.excel.core.IExcelCell> iterator() {
        return this.new Iter();
    }

    @Getter
    private class Iter implements Iterator<org.dreamcat.jwrap.excel.core.IExcelCell>,
            org.dreamcat.jwrap.excel.core.IExcelCell {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public IExcelCell next() {
            return null;
        }

        @Override
        public int getRowIndex() {
            return 0;
        }

        @Override
        public int getColumnIndex() {
            return 0;
        }

        @Override
        public IExcelContent getContent() {
            return null;
        }
    }
}
