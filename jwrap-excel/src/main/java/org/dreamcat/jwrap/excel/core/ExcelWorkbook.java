package org.dreamcat.jwrap.excel.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelWorkbook<T extends IExcelSheet> implements IExcelWorkbook<T> {

    private final List<T> sheets;
    private ExcelStyle defaultStyle;
    private ExcelFont defaultFont;

    public ExcelWorkbook() {
        this.sheets = new ArrayList<>();
    }

    public static ExcelWorkbook<ExcelSheet> from(File file)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> fromBigGrid(File file)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook(file))) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> from2003(File file) throws IOException {
        try (Workbook workbook = new HSSFWorkbook(new POIFSFileSystem(file, true))) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> from(Workbook workbook) {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();

        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            book.sheets.add(ExcelSheet.from(workbook, sheet));
        }
        return book;
    }

    @Override
    public Iterator<T> iterator() {
        return sheets.iterator();
    }

    @Override
    public ExcelWorkbook<T> add(T sheet) {
        sheets.add(sheet);
        return this;
    }
}
