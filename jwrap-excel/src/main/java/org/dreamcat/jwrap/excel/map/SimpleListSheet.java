package org.dreamcat.jwrap.excel.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.Getter;
import org.dreamcat.common.x.asm.BeanMapUtil;
import org.dreamcat.jwrap.excel.content.ExcelUnionContent;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelSheet;

/**
 * Create by tuke on 2020/7/22
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleListSheet implements IExcelSheet {

    private String name;
    // [Cell..., T1, Cell..., T2], it mixes Cell & Pojo up
    private final List schemes;

    public SimpleListSheet(String name) {
        this(name, new ArrayList<>(0));
    }

    public SimpleListSheet(String name, List schemes) {
        this.name = name;
        this.schemes = schemes;
    }

    public void add(Object row) {
        schemes.add(row);
    }

    public void addAll(Collection scheme) {
        schemes.addAll(scheme);
    }

    public void addCell(IExcelCell cell) {
        schemes.add(cell);
    }

    public void addHeader(Class<?> clazz) {
        XlsHeaderMeta meta = XlsHeaderMeta.parse(clazz);
        addAll(meta.getHeaderCells());
        this.name = meta.name;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    @Getter
    private class Iter extends ExcelUnionContent implements Iterator<IExcelCell>, IExcelCell {

        // as row index offset since row based structure
        int offset;
        int schemeSize;
        int schemeIndex;

        List row;
        int columnSize;
        int columnIndex;

        // only not null if scheme is a IExcelCell
        IExcelCell cell;
        IExcelCell nextCell;
        int maxRowOffset;

        private Iter() {
            offset = 0;
            if (schemes.isEmpty()) {
                clear(true);
                return;
            }
            schemeSize = schemes.size();
            schemeIndex = 0;
            columnIndex = -1;

            setRow(schemes.get(0));
            if (nextCell != null) {
                return;
            }

            if (row.isEmpty()) {
                clear(true);
                return;
            }
            columnSize = row.size();
        }

        @Override
        public IExcelContent getContent() {
            return this;
        }

        @Override
        public int getRowIndex() {
            if (cell != null) {
                return cell.getRowIndex() + offset;
            } else {
                return offset;
            }
        }

        @Override
        public int getColumnIndex() {
            if (cell != null) {
                return cell.getColumnIndex();
            } else {
                return columnIndex;
            }
        }

        @Override
        public int getRowSpan() {
            if (cell != null) {
                return cell.getRowSpan();
            }
            return 1;
        }

        @Override
        public int getColumnSpan() {
            if (cell != null) {
                return cell.getColumnSpan();
            }
            return 1;
        }

        @Override
        public boolean hasNext() {
            // empty schemes
            if (columnSize == -1) return false;
            // reach all schemes
            if (schemeIndex >= schemeSize) return false;
            // has cells
            if (nextCell != null) return true;

            return columnIndex < columnSize - 1 || schemeIndex < schemeSize - 1;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            // in cell case scheme
            if (nextCell != null) {
                setCellAndMove();
                return this;
            }

            // if next element is a POJO
            if (cell != null) {
                // clear cell bits
                cell = null;
                offset += maxRowOffset;
                maxRowOffset = 0;
            }

            // move magical cursor
            columnIndex++;
            if (columnIndex >= columnSize) {
                schemeIndex++;
                setRow(schemes.get(schemeIndex));
                // move offset
                offset++;
                columnIndex = -1;

                // next is a IExcelCell
                if (row == null) {
                    setCellAndMove();
                    return this;
                }
            }

            // Note that -1 if next is a IExcelCell
            if (columnIndex == -1) columnIndex = 0;

            // set content
            Object value = row.get(columnIndex);
            setContent(value);
            return this;
        }

        private void setCellAndMove() {
            // move magical cursor for cells
            cell = nextCell;
            maxRowOffset = Math.max(cell.getRowIndex() + cell.getRowSpan(), maxRowOffset);
            schemeIndex++;
            if (schemeIndex < schemeSize) {
                setRow(schemes.get(schemeIndex));
            } else {
                nextCell = null;
            }

            setRawContent(cell.getContent());
        }

        // Note that it makes hasNext() return false
        private void clear(boolean initial) {
            schemeSize = 0;
            columnSize = -1;
            if (initial) return;

            row = null;
            cell = null;
            nextCell = null;
        }

        private void setRow(Object rawRow) {
            if (rawRow instanceof IExcelCell) {
                nextCell = (IExcelCell) rawRow;
                row = null;
                return;
            } else if (rawRow instanceof List) {
                row = (List) (rawRow);
            } else if (rawRow instanceof Map) {
                row = new ArrayList(((Map<?, ?>) rawRow).values());
            } else {
                row = BeanMapUtil.toList(rawRow);
            }
            columnSize = row.size();
            nextCell = null;
        }

    }
}
