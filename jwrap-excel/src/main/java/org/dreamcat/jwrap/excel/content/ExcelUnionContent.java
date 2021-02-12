package org.dreamcat.jwrap.excel.content;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Create by tuke on 2020/7/22
 */
@SuppressWarnings("rawtypes")
public class ExcelUnionContent implements org.dreamcat.jwrap.excel.content.IExcelContent {

    private final org.dreamcat.jwrap.excel.content.ExcelStringContent stringContent;
    private final org.dreamcat.jwrap.excel.content.ExcelNumericContent numericContent;
    private final ExcelBooleanContent booleanContent;
    private transient org.dreamcat.jwrap.excel.content.IExcelContent rawContent;
    private transient Class type;

    protected ExcelUnionContent() {
        this.stringContent = new org.dreamcat.jwrap.excel.content.ExcelStringContent();
        this.numericContent = new org.dreamcat.jwrap.excel.content.ExcelNumericContent();
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
        this.stringContent.setValue(value);
        this.type = org.dreamcat.jwrap.excel.content.ExcelStringContent.class;
    }

    public void setNumericContent(double value) {
        this.numericContent.setValue(value);
        this.type = org.dreamcat.jwrap.excel.content.ExcelNumericContent.class;
    }

    public void setBooleanContent(boolean value) {
        this.booleanContent.setValue(value);
        this.type = ExcelBooleanContent.class;
    }

    public void setRawContent(org.dreamcat.jwrap.excel.content.IExcelContent rawContent) {
        this.rawContent = rawContent;
        this.type = org.dreamcat.jwrap.excel.content.IExcelContent.class;
    }

    public void setContent(Object value) {
        if (value instanceof Number) {
            Number number = (Number) value;
            setNumericContent(number.doubleValue());
        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean) value;
            setBooleanContent(bool);
        } else if (value instanceof org.dreamcat.jwrap.excel.content.IExcelContent) {
            setRawContent((IExcelContent) value);
        } else {
            setStringContent(value == null ? "" : value.toString());
        }
    }

    @Override
    public String toString() {
        if (type.equals(org.dreamcat.jwrap.excel.content.ExcelStringContent.class)) {
            return stringContent.getValue();
        } else if (type.equals(org.dreamcat.jwrap.excel.content.ExcelNumericContent.class)) {
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
