package org.dreamcat.jwrap.excel.content;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.dreamcat.jwrap.excel.style.ExcelRichString;

/**
 * Create by tuke on 2020/7/21
 */
public interface IExcelContent {

    static IExcelContent from(Cell cell) {
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                RichTextString richTextString = cell.getRichStringCellValue();
                return new ExcelStringContent(ExcelRichString.from(richTextString));
            case NUMERIC:
                return new ExcelNumericContent(cell.getNumericCellValue());
            case BOOLEAN:
                return new ExcelBooleanContent(cell.getBooleanCellValue());
            case FORMULA:
                return new ExcelFormulaContent(cell.getCellFormula());
            default:
                return new ExcelStringContent();
        }
    }

    static Object valueOf(Cell cell) {
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                // string
                return cell.getStringCellValue();
            case NUMERIC:
                // double
                double value = cell.getNumericCellValue();
                long round = Math.round(value);
                if (value == (double) round) {
                    return round;
                }
                return value;
            case BOOLEAN:
                // boolean
                return cell.getBooleanCellValue();
            case FORMULA:
                // string
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    void fill(Cell cell);

}
