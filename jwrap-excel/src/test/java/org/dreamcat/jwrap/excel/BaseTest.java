package org.dreamcat.jwrap.excel;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.dreamcat.common.function.ThrowableBiConsumer;
import org.dreamcat.common.function.ThrowableConsumer;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.dreamcat.jwrap.excel.core.IExcelSheet;
import org.dreamcat.jwrap.excel.core.IExcelWorkbook;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2021/2/15
 */
public interface BaseTest {

    String basePath = System.getenv("HOME") + "/Downloads";

    File baseDir = new File(basePath);

    /// pattern

    default <T extends IExcelSheet> void writeXlsxWithBigGrid(String prefix, T... sheets) {
        writeExcel(prefix, "xlsx", IExcelWorkbook::writeToWithBigGrid, sheets);
    }

    default <T extends IExcelSheet> void writeXlsx(String prefix, T... sheets) {
        writeExcel(prefix, "xlsx", IExcelWorkbook::writeTo, sheets);
    }

    default <T extends IExcelSheet> void writeXlsx(
            ExcelWorkbook<T> book, String prefix, T... sheets) {
        writeExcel(book, prefix, "xlsx", sheets);
    }

    default <T extends IExcelSheet> void writeExcel(
            ExcelWorkbook<T> book, String prefix, String suffix, T... sheets) {
        writeExcel(book, prefix, suffix, IExcelWorkbook::writeTo, sheets);
    }

    default <T extends IExcelSheet> void writeExcel(
            String prefix, String suffix,
            ThrowableBiConsumer<IExcelWorkbook<?>, File> writer, T... sheets) {
        ExcelWorkbook<T> book = new ExcelWorkbook<>();
        writeExcel(book, prefix, suffix, writer, sheets);
    }

    default <T extends IExcelSheet> void writeExcel(
            ExcelWorkbook<T> book, String prefix, String suffix,
            ThrowableBiConsumer<IExcelWorkbook<?>, File> writer, T... sheets) {
        File file = new File(baseDir, prefix + "." + suffix);
        System.out.printf("writing to %s\n", file);
        try {
            writer.accept(book.addSheets(Arrays.asList(sheets)), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void readXlsx(String prefix,
            ThrowableConsumer<ExcelSheet> callback) {
        readExcel(prefix, "xlsx", callback);
    }

    default void readExcel(String prefix, String suffix,
            ThrowableConsumer<ExcelSheet>  callback) {
        ExcelWorkbook<ExcelSheet> book;
        try {
            book = ExcelWorkbook.from(new File(baseDir, prefix + "." + suffix));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<ExcelSheet> sheets = book.getSheets();
        for (ExcelSheet sheet : sheets) {
            try {
                callback.accept(sheet);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /// util

    default void printSheet(IExcelSheet sheet) {
        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent());
        }
    }

    default void printSheetVerbose(IExcelSheet sheet) {
        for (IExcelCell cell : sheet) {
            ExcelFont font = null;
            ExcelStyle style = cell.getStyle();
            if (style != null) {
                font = style.getFont();
            }

            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    font, style
            );
        }
    }
}
