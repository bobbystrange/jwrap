package org.dreamcat.jwrap.excel.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelSheet;
import org.dreamcat.jwrap.excel.core.IExcelWriteCallback;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/26
 * <p>
 * It is a very tricky implementation to translate annotated beans to sheet interface,
 * which simplifies the API usage without sacrificing memory
 * Note that it is thread-unsafe in the iteration however
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationListSheet implements IExcelSheet {

    private String name;
    // [Sheet..., T1..., Sheet..., T2...], it mixes Sheet & Pojo up
    private final List schemes;
    // Note that it maybe create more than 64000 cell styles on one sheet, that will cause a error
    @Setter
    private boolean annotationStyle;
    @Setter
    private IExcelWriteCallback writeCallback;

    public AnnotationListSheet(String name) {
        this(name, new ArrayList<>(0));
    }

    public AnnotationListSheet(String name, List schemes) {
        this.name = name;
        this.schemes = schemes;
    }

    public void add(Object row) {
        schemes.add(row);
    }

    public void addAll(Collection scheme) {
        schemes.addAll(scheme);
    }

    public void addSheet(IExcelSheet sheet) {
        schemes.add(sheet);
    }

    public void addHeader(Class<?> clazz) {
        XlsHeaderMeta meta = XlsHeaderMeta.parse(clazz);
        addSheet(meta);
        this.name = meta.name;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    @Override
    public IExcelWriteCallback writeCallback() {
        return writeCallback;
    }

    @Getter
    private class Iter implements Iterator<IExcelCell>,
            IExcelCell {

        // as row index offset since row based structure
        int offset;
        int schemeSize;
        int schemeIndex;

        IExcelCell cell;
        int maxRowOffset;
        Iterator<IExcelCell> iterator;
        // whether next is in row sheet iter case or not
        boolean nextInRowSheetIterCase;
        // just switch row sheet to iterator
        boolean inSwitchIterCase;
        AnnotationRowSheet.Iter rowSheetIter;

        private Iter() {
            schemeSize = schemes.size();
            if (schemeSize == 0) return;
            move();
        }

        @Override
        public IExcelContent getContent() {
            return cell.getContent();
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
        public ExcelStyle getStyle() {
            if ((nextInRowSheetIterCase || inSwitchIterCase) && !annotationStyle) return null;
            return cell.getStyle();
        }

        @Override
        public boolean hasNext() {
            if (schemeSize == 0) return false;
            if (maxRowOffset < 0) {
                offset -= maxRowOffset;
                maxRowOffset = 0;
            }
            return (iterator != null && iterator.hasNext()) ||
                    (nextInRowSheetIterCase && rowSheetIter != null && rowSheetIter.hasNext());
        }

        @Override
        public IExcelCell next() {
            inSwitchIterCase = false;
            if (!nextInRowSheetIterCase && iterator != null) {
                // prepare cell
                cell = iterator.next();
                maxRowOffset = Math.max(maxRowOffset, cell.getRowSpan());

                if (iterator.hasNext()) return this;
            }

            if (nextInRowSheetIterCase && rowSheetIter != null) {
                // prepare cell
                cell = rowSheetIter.next();
                maxRowOffset = Math.max(maxRowOffset, cell.getRowSpan());

                if (rowSheetIter.hasNext()) return this;
            }

            iterator = null;
            schemeIndex++;
            if (schemeIndex < schemeSize) {
                move();
            } else {
                // end of iteration
                nextInRowSheetIterCase = false;
                rowSheetIter = null;
                inSwitchIterCase = true;
            }
            return this;
        }

        // move magical cursor for cells
        private void move() {
            maxRowOffset = -maxRowOffset;

            inSwitchIterCase = nextInRowSheetIterCase;
            Object rawScheme = schemes.get(schemeIndex);
            if (rawScheme instanceof IExcelSheet) {
                iterator = ((IExcelSheet) rawScheme).iterator();
                nextInRowSheetIterCase = false;
            } else {
                if (rowSheetIter == null) {
                    AnnotationRowSheet rowSheet = new AnnotationRowSheet(rawScheme);
                    rowSheetIter = rowSheet.new Iter();
                } else {
                    rowSheetIter.reset(rawScheme);
                }
                nextInRowSheetIterCase = true;
            }
            // nextInRowSheetIterCase is modified from true to false
            inSwitchIterCase = inSwitchIterCase && !nextInRowSheetIterCase;
        }
    }

}
