package org.dreamcat.jwrap.excel.map.serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dreamcat.jwrap.excel.content.ExcelStringContent;
import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2021/2/4
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SerializablePieceHead extends ExcelStringContent implements IExcelCell {

    protected int rowIndex;
    protected int columnIndex;
    protected int rowSpan;
    protected int columnSpan;
    protected ExcelFont font;
    protected ExcelStyle style;
    protected ExcelHyperLink hyperLink;

    @Override
    public IExcelContent getContent() {
        return this;
    }
}
