package org.dreamcat.jwrap.excel.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.dreamcat.jwrap.excel.style.ExcelRichString;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelStringContent implements IExcelContent {

    private ExcelRichString value;

    @Override
    public void fill(Cell cell) {
        if (!value.hasFormatting()) {
            cell.setCellValue(value.getString());
            return;
        }

        String string = value.getString();
        RichTextString richTextString;
        if (cell instanceof XSSFCell) {
            richTextString = new XSSFRichTextString(string);
        } else {
            richTextString = new HSSFRichTextString(string);
        }
        value.fill(richTextString);
        cell.setCellValue(richTextString);
    }

    @Override
    public String toString() {
        return value.getString();
    }

    public static ExcelStringContent from(String string) {
        ExcelRichString value = new ExcelRichString(string, null);
        return new ExcelStringContent(value);
    }
}
