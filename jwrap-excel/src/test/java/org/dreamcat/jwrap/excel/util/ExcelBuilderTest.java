package org.dreamcat.jwrap.excel.util;

import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;
import static org.dreamcat.jwrap.excel.util.ExcelBuilder.sheet;
import static org.dreamcat.jwrap.excel.util.ExcelBuilder.term;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.jwrap.excel.BaseTest;
import org.dreamcat.jwrap.excel.callback.FitWidthWriteCallback;
import org.dreamcat.jwrap.excel.callback.LoggingWriteCallback;
import org.dreamcat.jwrap.excel.core.ExcelCell;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.style.ExcelClientAnchor;
import org.dreamcat.jwrap.excel.style.ExcelComment;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;
import org.dreamcat.jwrap.excel.style.ExcelRichString;
import org.dreamcat.jwrap.excel.style.ExcelStyle;
import org.dreamcat.jwrap.excel.util.ExcelBuilder.SheetTerm;
import org.junit.Test;


/**
 * Create by tuke on 2020/7/22
 */
public class ExcelBuilderTest implements BaseTest {

    public static SheetTerm headerSheet() {
        return sheet("Sheet Cell")
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
                .finishCell();
    }

    @Test
    public void testSmall() throws Exception {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();
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

            ExcelStyle style = new ExcelStyle();
            style.setFgColor(IndexedColors.ROSE.getIndex());
            style.setHorizontalAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFont(font);

            ExcelCell cell = new ExcelCell(
                    term(rand() * (1 << 10)),
                    i, 0, 1, 1)
                    .setStyle(style)
                    .setHyperLink(new ExcelHyperLink(
                            HyperlinkType.URL, "http://marry.me", "link"))
                    .setComment(new ExcelComment(
                            true, "tuke",
                            ExcelRichString.from("awesom"),
                            new ExcelClientAnchor(AnchorType.MOVE_AND_RESIZE,
                                    0, 0, 0, 0,
                                    0, i, 0, i)));
            sheet.getCells().add(cell);
        }
        sheet.setWriteCallback(new FitWidthWriteCallback());

        book.addSheet(sheet);
        book.writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Test
    public void test() throws Exception {
        SheetTerm sheetTerm = headerSheet();
        ExcelSheet headerSheet = sheetTerm.finish();
        headerSheet.setWriteCallback(new LoggingWriteCallback());
        printSheetVerbose(headerSheet);
        writeXlsx("book_ExcelBuilderTest_test", headerSheet);
    }
}
