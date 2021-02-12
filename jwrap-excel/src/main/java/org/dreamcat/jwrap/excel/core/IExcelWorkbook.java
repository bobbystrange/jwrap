package org.dreamcat.jwrap.excel.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelWorkbook<T extends IExcelSheet> extends Iterable<T> {

    List<T> getSheets();

    default org.dreamcat.jwrap.excel.style.ExcelStyle getDefaultStyle() {
        return null;
    }

    default org.dreamcat.jwrap.excel.style.ExcelFont getDefaultFont() {
        return null;
    }

    IExcelWorkbook<T> add(T sheet);

    default XSSFWorkbook toWorkbook() {
        return toWorkbook(new XSSFWorkbook());
    }

    default SXSSFWorkbook toWorkbookWithBigGrid() {
        return toWorkbook(new SXSSFWorkbook());
    }

    default HSSFWorkbook toWorkbook2003() {
        return toWorkbook(new HSSFWorkbook());
    }

    default <W extends Workbook> W toWorkbook(W workbook) {
        Font font = workbook.createFont();
        ExcelFont defaultFont = getDefaultFont();
        if (defaultFont != null) defaultFont.fill(font);

        CellStyle style = workbook.createCellStyle();
        ExcelStyle defaultStyle = getDefaultStyle();
        if (defaultStyle != null) defaultStyle.fill(style, font);

        int sheetIndex = 0;
        for (T excelSheet : this) {
            Sheet sheet = workbook.createSheet(excelSheet.getName());
            excelSheet.fill(workbook, sheet, sheetIndex++, style, font);
        }
        return workbook;
    }

    default void writeTo(String newFile) throws IOException {
        writeTo(new File(newFile));
    }

    default void writeTo(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeTo(ostream);
        }
    }

    default void writeTo(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbook()) {
            workbook.write(output);
        }
    }

    default void writeToWithBigGrid(String newFile) throws IOException {
        writeToWithBigGrid(new File(newFile));
    }

    default void writeToWithBigGrid(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeToWithBigGrid(ostream);
        }
    }

    default void writeToWithBigGrid(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbookWithBigGrid()) {
            workbook.write(output);
        }
    }

    default void writeTo2003(String newFile) throws IOException {
        writeTo2003(new File(newFile));
    }

    default void writeTo2003(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeTo2003(ostream);
        }
    }

    default void writeTo2003(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbook2003()) {
            workbook.write(output);
        }
    }

    default byte[] toByteArray() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbook()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

    default byte[] toByteArrayWithBigGrid() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbookWithBigGrid()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

    default byte[] toByteArray2003() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbook2003()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

}
