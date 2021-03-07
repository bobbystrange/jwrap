package org.dreamcat.jwrap.excel.core;

import java.util.Iterator;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Create by tuke on 2021/2/16
 */
@Data
public class DelegateSheet implements IExcelSheet {

    private final IExcelSheet delegate;
    private String name;
    private IExcelWriteCallback writeCallback;

    public DelegateSheet(IExcelSheet delegate) {
        this.delegate = delegate;
        this.name = delegate.getName();
        this.writeCallback = delegate.writeCallback();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public IExcelWriteCallback writeCallback() {
        return writeCallback;
    }

    @Override
    public void fill(Sheet sheet, int sheetIndex, IExcelWorkbook<?> excelWorkbook) {
        delegate.fill(sheet, sheetIndex, excelWorkbook);
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return delegate.iterator();
    }
}
