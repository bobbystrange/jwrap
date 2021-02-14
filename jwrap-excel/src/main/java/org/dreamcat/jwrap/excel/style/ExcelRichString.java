package org.dreamcat.jwrap.excel.style;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2021/2/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelRichString {

    private String string;
    private List<FormattingRun> formattingRuns;

    public static ExcelRichString from(String string) {
        return new ExcelRichString(string, null);
    }

    public static ExcelRichString from(
            RichTextString richTextString) {
        List<FormattingRun> formattingRuns;
        String string = richTextString.getString();
        int numFormattingRuns = richTextString.numFormattingRuns();

        if (numFormattingRuns == 0) {
            return from(string);
        }

        formattingRuns = new ArrayList<>(numFormattingRuns);
        int offset = 0;
        for (int i = 0; i < numFormattingRuns; i++) {
            int index = richTextString.getIndexOfFormattingRun(i);
            String substring = string.substring(offset, index);
            offset = index;
            FormattingRun formattingRun = new FormattingRun(substring);

            short fontIndex = HSSFRichTextString.NO_FONT;
            if (richTextString instanceof HSSFRichTextString) {
                HSSFRichTextString hssfRichTextString = (HSSFRichTextString) richTextString;
                fontIndex = hssfRichTextString.getFontAtIndex(i);
            } else {
                XSSFRichTextString xssfRichTextString = (XSSFRichTextString) richTextString;
                XSSFFont xssfFont = xssfRichTextString.getFontOfFormattingRun(i);
                if (xssfFont != null) {
                    fontIndex = (short) xssfFont.getIndex();
                }
            }
            formattingRun.setFontIndex(fontIndex);
            formattingRuns.add(formattingRun);
        }
        return new ExcelRichString(string, formattingRuns);
    }

    public void fill(RichTextString richTextString) {
        if (ObjectUtil.isEmpty(formattingRuns)) return;
        int startIndex = 0;
        int endIndex = 0;
        for (FormattingRun formattingRun : formattingRuns) {
            String substring = formattingRun.getString();
            endIndex += substring.length();

            short fontIndex = formattingRun.getFontIndex();
            richTextString.applyFont(startIndex, endIndex, fontIndex);
            startIndex = endIndex;
        }
    }

    public boolean hasFormatting() {
        return ObjectUtil.isNotEmpty(formattingRuns);
    }
}
