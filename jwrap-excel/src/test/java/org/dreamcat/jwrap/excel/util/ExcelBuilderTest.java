package org.dreamcat.jwrap.excel.util;

import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;
import static org.dreamcat.jwrap.excel.util.ExcelBuilder.sheet;
import static org.dreamcat.jwrap.excel.util.ExcelBuilder.term;
import static org.dreamcat.jwrap.excel.util.ExcelBuilder.workbook;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.jwrap.excel.callback.FitWidthWriteCallback;
import org.dreamcat.jwrap.excel.core.ExcelRichCell;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.core.ExcelWorkbookTest;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelRichStyle;
import org.junit.Test;


/**
 * Create by tuke on 2020/7/22
 */
public class ExcelBuilderTest {

    public static ExcelSheet headerSheet() {
        return sheet("Sheet Header")
                // col1
                .richCell(term("A1:A2"), 0, 0, 2, 1)
                .height(24)
                .color(IndexedColors.RED1.getIndex())
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .fgColor(IndexedColors.ROSE.getIndex())
                .finishCell()
                // col2
                .richCell("B1", 0, 1, 2, 1)
                .height(32)
                .fgColor(IndexedColors.VIOLET.getIndex())
                .finishCell()
                // col3
                .richCell(term("C1:D1"), 0, 2, 1, 2)
                .height(16)
                .verticalAlignment(VerticalAlignment.CENTER)
                .fgColor(IndexedColors.LEMON_CHIFFON.getIndex())
                .finishCell()
                .richCell("C2", 1, 2)
                .height(14)
                .fgColor(IndexedColors.GREY_50_PERCENT.getIndex())
                .finishCell()
                .richCell("D2", 1, 3)
                .fgColor(IndexedColors.LAVENDER.getIndex())
                .finishCell()
                // col4
                .richCell(term("E1:F1"), 0, 4, 1, 2)
                .height(12)
                .verticalAlignment(VerticalAlignment.CENTER)
                .fgColor(IndexedColors.AQUA.getIndex())
                .finishCell()
                .richCell("E2", 1, 4)
                .height(10)
                .fgColor(IndexedColors.OLIVE_GREEN.getIndex())
                .finishCell()
                .richCell("F2", 1, 5)
                .height(8)
                .fgColor(IndexedColors.PALE_BLUE.getIndex())
                .finishCell()
                .finishSheet();
    }

    @Test
    public void testSmall() throws Exception {
        ExcelSheet sheet = new ExcelSheet("Sheet One");

        IndexedColors[] colors = new IndexedColors[]{
                IndexedColors.BLUE_GREY,
                IndexedColors.BRIGHT_GREEN,
                IndexedColors.DARK_BLUE,
                IndexedColors.DARK_YELLOW,
        };
        for (int i = 0; i < 12; i++) {
            ExcelFont font = new ExcelFont();
            font.setHeight((short) 32);
            font.setBold(i % 2 == 0);
            font.setItalic(i % 3 == 0);
            font.setColor(colors[randi(128) % 4].getIndex());

            ExcelRichStyle style = new ExcelRichStyle();
            style.setFgColor(IndexedColors.ROSE.getIndex());
            // style.setHorizontalAlignment(HorizontalAlignment.CENTER);
            // style.setVerticalAlignment(VerticalAlignment.CENTER);

            sheet.getCells().add(new ExcelRichCell(
                    term(rand() * (1 << 10)),
                    i, 0, 1, 1,
                    font, style, null));
        }
        sheet.setWriteCallback(new FitWidthWriteCallback());

        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();
        book.add(sheet);
        book.writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Test
    public void test() throws Exception {
        ExcelSheet headerSheet = headerSheet();
        ExcelWorkbookTest.printVerbose(headerSheet);

        // headerSheet.setWriteCallback(new LoggingWriteCallback());
        ExcelWorkbook<ExcelSheet> book = workbook()
                .addSheet(headerSheet)
                .finish();
        book.writeTo("/Users/tuke/Downloads/book.xlsx");
    }
}
