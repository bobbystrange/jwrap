package org.dreamcat.jwrap.excel.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.x.bean.BeanMapUtil;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelSheet;

/**
 * Create by tuke on 2020/7/22
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleSheet implements IExcelSheet {

    private String name;
    // [Sheet..., T1..., Sheet..., T2...], it mixes Sheet & Pojo up
    private final List schemes;
    @Setter
    private Function<Object, List<?>> schemeConverter = BeanMapUtil::toList;

    public SimpleSheet(String name) {
        this(name, new ArrayList<>(0));
    }

    public SimpleSheet(String name, List schemes) {
        this.name = name;
        this.schemes = schemes;
    }

    public void addRow(IExcelSheet row) {
        schemes.add(row);
    }

    public void addRow(Object row) {
        schemes.add(row);
    }

    public void addAll(Collection scheme) {
        schemes.addAll(scheme);
    }

    public void addHeader(Class<?> clazz) {
        XlsHeaderMeta meta = XlsHeaderMeta.parse(clazz);
        addRow(new ExcelSheet(meta.name, meta.getHeaderCells()));
        this.name = meta.name;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    private class Iter implements Iterator<IExcelCell>, IExcelCell {

        // as row index offset since row based structure
        int offset;
        int schemeSize;
        int schemeIndex;

        IExcelCell cell;
        Iterator<IExcelCell> sheetIter;
        SimpleRowSheet sheet;
        int maxRowOffset;
        boolean inSwitchOffsetCase;

        private Iter() {
            offset = 0;
            if (schemes.isEmpty()) {
                clear();
                return;
            }

            schemeIndex = 0;
            schemeSize = schemes.size();
            move();
        }

        @Override
        public int getRowIndex() {
            return cell.getRowIndex() + offset;
        }

        @Override
        public int getColumnIndex() {
            return cell.getColumnIndex();
        }

        @Override
        public int getRowSpan() {
            return cell.getRowSpan();
        }

        @Override
        public int getColumnSpan() {
            return cell.getColumnSpan();
        }

        @Override
        public IExcelContent getContent() {
            return cell.getContent();
        }

        @Override
        public boolean hasNext() {
            return schemeIndex < schemeSize;
        }

        @Override
        public IExcelCell next() {
            if (!hasNext()) throw new NoSuchElementException();

            cell = sheetIter.next();
            if (inSwitchOffsetCase) {
                offset += maxRowOffset;
                inSwitchOffsetCase = false;
                maxRowOffset = 0;
            }

            // update the max row offset
            maxRowOffset = Math.max(cell.getRowIndex() + cell.getRowSpan(), maxRowOffset);

            if (!sheetIter.hasNext()) {
                inSwitchOffsetCase = true;
                schemeIndex++;
                if (schemeIndex >= schemeSize) {
                    clear();
                } else {
                    move();
                }
            }
            return this;
        }

        // Note that it makes hasNext() return false
        private void clear() {
            schemeIndex = 0;
            schemeSize = 0;
            sheetIter = null;
        }

        private void move() {
            for (; ; ) {
                Object rawRow = schemes.get(schemeIndex);
                if (rawRow instanceof IExcelSheet) {
                    sheetIter = ((IExcelSheet) rawRow).iterator();
                } else {
                    if (sheet != null) {
                        sheet.reset(rawRow);
                    } else {
                        sheet = new SimpleRowSheet(name, rawRow);
                        sheet.setSchemeConverter(schemeConverter);
                    }
                    sheetIter = sheet.iterator();
                }
                if (sheetIter.hasNext()) break;

                // reach a empty sheet, then skip it
                schemeIndex++;
                if (schemeIndex >= schemeSize) {
                    clear();
                    return;
                }
            }
        }
    }

}
