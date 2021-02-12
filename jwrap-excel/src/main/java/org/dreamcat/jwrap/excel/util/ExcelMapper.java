package org.dreamcat.jwrap.excel.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.util.ArrayUtil;
import org.dreamcat.common.util.StringUtil;
import org.dreamcat.jwrap.excel.content.IExcelContent;

/**
 * Create by tuke on 2020/8/13
 */
public final class ExcelMapper {

    private ExcelMapper() {
    }

    public static List<List<List<String>>> parseAsString(String filename)
            throws IOException, InvalidFormatException {
        return ArrayUtil.map(parse(filename), list -> list == null ? null : list.stream()
                .map(StringUtil::toString)
                .collect(Collectors.toList()));
    }

    public static List<List<List<String>>> parseAsString(File file)
            throws IOException, InvalidFormatException {
        return ArrayUtil.map(parse(file), list -> list == null ? null : list.stream()
                .map(StringUtil::toString)
                .collect(Collectors.toList()));
    }

    public static List<List<List<String>>> parseAsString(InputStream input) throws IOException {
        return ArrayUtil.map(parse(input), list -> list == null ? null : list.stream()
                .map(StringUtil::toString)
                .collect(Collectors.toList()));
    }

    public static List<List<List<String>>> parseAsString(Workbook workbook) {
        return ArrayUtil.map(parse(workbook), list -> list == null ? null : list.stream()
                .map(StringUtil::toString)
                .collect(Collectors.toList()));
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static List<List<String>> parseAsString(String filename, int sheetIndex)
            throws IOException, InvalidFormatException {
        return ArrayUtil.map(parse(filename, sheetIndex), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(File file, int sheetIndex)
            throws IOException, InvalidFormatException {
        return ArrayUtil.map(parse(file, sheetIndex), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(InputStream input, int sheetIndex)
            throws IOException {
        return ArrayUtil.map(parse(input, sheetIndex), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(String filename, String sheetName)
            throws IOException, InvalidFormatException {
        return ArrayUtil.map(parse(filename, sheetName), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(File file, String sheetName)
            throws IOException, InvalidFormatException {
        return ArrayUtil.map(parse(file, sheetName), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(InputStream input, String sheetName)
            throws IOException {
        return ArrayUtil.map(parse(input, sheetName), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(Workbook workbook, int sheetIndex) {
        return ArrayUtil.map(parse(workbook, sheetIndex), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(Workbook workbook, String sheetName) {
        return ArrayUtil.map(parse(workbook, sheetName), StringUtil::toString);
    }

    public static List<List<String>> parseAsString(Sheet sheet) {
        return ArrayUtil.map(parse(sheet), StringUtil::toString);
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static List<List<List<Object>>> parse(String filename)
            throws IOException, InvalidFormatException {
        return parse(new File(filename));
    }

    public static List<List<List<Object>>> parse(File file)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return parse(workbook);
        }
    }

    public static List<List<List<Object>>> parse(InputStream input) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            return parse(workbook);
        }
    }

    public static List<List<List<Object>>> parse(Workbook workbook) {
        int sheetNum = workbook.getNumberOfSheets();
        List<List<List<Object>>> sheets = new ArrayList<>(sheetNum);
        if (sheetNum == 0) return sheets;

        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheets.add(parse(sheet));
        }
        return sheets;
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public static List<List<Object>> parse(String filename, int sheetIndex)
            throws IOException, InvalidFormatException {
        return parse(new File(filename), sheetIndex);
    }

    public static List<List<Object>> parse(File file, int sheetIndex)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return parse(workbook, sheetIndex);
        }
    }

    public static List<List<Object>> parse(InputStream input, int sheetIndex) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            return parse(workbook, sheetIndex);
        }
    }

    public static List<List<Object>> parse(String filename, String sheetName)
            throws IOException, InvalidFormatException {
        return parse(new File(filename), sheetName);
    }

    public static List<List<Object>> parse(File file, String sheetName)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return parse(workbook, sheetName);
        }
    }

    public static List<List<Object>> parse(InputStream input, String sheetName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            return parse(workbook, sheetName);
        }
    }

    public static List<List<Object>> parse(Workbook workbook, int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) return null;
        return parse(sheet);
    }

    public static List<List<Object>> parse(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return null;
        return parse(sheet);
    }

    /**
     * parse a sheet to 2-D list contains String/Double/Boolean
     *
     * @param sheet excel sheet
     * @return 2-D list
     * @see IExcelContent#valueOf(Cell)
     */
    public static List<List<Object>> parse(Sheet sheet) {
        int rowNum = sheet.getLastRowNum();
        List<List<Object>> rowValues = new ArrayList<>();
        for (int i = 0; i <= rowNum; i++) {
            Row row = sheet.getRow(i);
            // the row is not defined on the sheet
            if (row == null) {
                rowValues.add(null);
                continue;
            }

            int end = row.getLastCellNum();

            List<Object> columnValues = new ArrayList<>();
            for (int j = 0; j < end; j++) {
                Cell cell = row.getCell(j);
                // undefined cell
                if (cell == null) {
                    columnValues.add(null);
                    continue;
                }

                columnValues.add(IExcelContent.valueOf(cell));
            }
            rowValues.add(columnValues);
        }
        return rowValues;
    }
}
