package org.dreamcat.jwrap.excel.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.jwrap.excel.content.IExcelContent;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelCell implements IExcelCell {

    protected IExcelContent content;
    protected int rowIndex;
    protected int columnIndex;
    protected int rowSpan;
    protected int columnSpan;

    public ExcelCell(IExcelContent content, int rowIndex, int columnIndex) {
        this(content, rowIndex, columnIndex, 1, 1);
    }
}
