package org.dreamcat.jwrap.excel.content;

import org.apache.poi.ss.usermodel.Cell;
import org.dreamcat.jwrap.excel.style.ExcelRichString;

/**
 * Create by tuke on 2020/7/22
 */
@SuppressWarnings("rawtypes")
public class ExcelUnionContent implements IExcelContent {

    private final ExcelStringContent stringContent;
    private final ExcelNumericContent numericContent;
    private final ExcelBooleanContent booleanContent;
    // transient
    private IExcelContent rawContent;
    // transient
    private Class type;

    protected ExcelUnionContent() {
        this.stringContent = new ExcelStringContent();
        this.numericContent = new ExcelNumericContent();
        this.booleanContent = new ExcelBooleanContent();
    }

    public ExcelUnionContent(String value) {
        this();
        setStringContent(value);
    }

    public ExcelUnionContent(double value) {
        this();
        setNumericContent(value);
    }

    public ExcelUnionContent(boolean value) {
        this();
        setBooleanContent(value);
    }

    public ExcelUnionContent(Object value) {
        this();
        setContent(value);
    }

    public void setStringContent(String value) {
        this.stringContent.setValue(ExcelRichString.from(value));
        this.type = ExcelStringContent.class;
    }

    public void setNumericContent(double value) {
        this.numericContent.setValue(value);
        this.type = ExcelNumericContent.class;
    }

    public void setBooleanContent(boolean value) {
        this.booleanContent.setValue(value);
        this.type = ExcelBooleanContent.class;
    }

    public void setRawContent(IExcelContent rawContent) {
        this.rawContent = rawContent;
        this.type = IExcelContent.class;
    }

    public void setContent(Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            setNumericContent(number.doubleValue());
        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean) value;
            setBooleanContent(bool);
        } else if (value instanceof IExcelContent) {
            setRawContent((IExcelContent) value);
        } else {
            setStringContent(value == null ? "" : value.toString());
        }
    }

    @Override
    public String toString() {
        if (type.equals(ExcelStringContent.class)) {
            return stringContent.getValue().getString();
        } else if (type.equals(ExcelNumericContent.class)) {
            return String.valueOf(numericContent.getValue());
        } else if (type.equals(ExcelBooleanContent.class)) {
            return String.valueOf(booleanContent.isValue());
        } else {
            return rawContent.toString();
        }
    }

    @Override
    public void fill(Cell cell) {
        if (type.equals(ExcelStringContent.class)) {
            stringContent.fill(cell);
        } else if (type.equals(ExcelNumericContent.class)) {
            numericContent.fill(cell);
        } else if (type.equals(ExcelBooleanContent.class)) {
            booleanContent.fill(cell);
        } else if (rawContent != null) {
            rawContent.fill(cell);
        }
    }
}
