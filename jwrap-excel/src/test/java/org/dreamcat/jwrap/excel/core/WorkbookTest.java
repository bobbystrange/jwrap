package org.dreamcat.jwrap.excel.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dreamcat.jwrap.excel.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Create by tuke on 2020/8/13
 */
@Ignore
public class WorkbookTest implements BaseTest {

    public static void read(File excelFile) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(excelFile)) {
            read(workbook);
        }
    }

    public static void read(Workbook workbook) {
        int numberOfNames = workbook.getNumberOfNames();
        System.out.println("numberOfNames: " + numberOfNames);
        workbook.getAllNames().forEach(System.out::println);
        System.out.println();

        int numberOfSheets = workbook.getNumberOfSheets();
        System.out.println("numberOfSheets: " + numberOfSheets);
        for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            readSheet(sheet, sheetIndex);
        }

    }

    public static void readSheet(Sheet sheet, int sheetIndex) {
        String sheetName = sheet.getSheetName();
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        System.out.printf("%dth sheet [%s]: firstRowNum = %d, lastRowNum = %d\n",
                sheetIndex, sheetName, firstRowNum, lastRowNum);

        for (int rowIndex = firstRowNum; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            System.out.printf("%dth sheet [%s] %dth row: firstCellNum = %d, lastCellNum = %d\n",
                    sheetIndex, sheetName, rowIndex, firstCellNum, lastCellNum);

            for (short columnIndex = firstCellNum; columnIndex < lastCellNum; columnIndex++) {
                Cell cell = row.getCell(columnIndex);
                if (cell == null) continue;

                Object value;
                CellType type = cell.getCellType();
                switch (type) {
                    case STRING:
                        value = cell.getStringCellValue();
                        System.out.printf("%dth sheet [%s] %dth row %dth column: string [%s]\n",
                                sheetIndex, sheetName, rowIndex, columnIndex, value.toString());
                        break;
                    case NUMERIC:
                        value = cell.getNumericCellValue();
                        System.out.printf("%dth sheet [%s] %dth row %dth column: numeric [%s]\n",
                                sheetIndex, sheetName, rowIndex, columnIndex, value.toString());
                        break;
                    case BOOLEAN:
                        value = cell.getBooleanCellValue();
                        System.out.printf("%dth sheet [%s] %dth row %dth column: boolean [%s]\n",
                                sheetIndex, sheetName, rowIndex, columnIndex, value.toString());
                        break;
                    case FORMULA:
                        value = cell.getCellFormula();
                        System.out.printf("%dth sheet [%s] %dth row %dth column: formula [%s]\n",
                                sheetIndex, sheetName, rowIndex, columnIndex, value.toString());
                        break;
                    case BLANK:
                        System.out.printf("%dth sheet [%s] %dth row %dth column: blank\n",
                                sheetIndex, sheetName, rowIndex, columnIndex);
                        break;
                    case ERROR:
                        value = cell.getErrorCellValue();
                        System.out.printf("%dth sheet [%s] %dth row %dth column: error [%s]\n",
                                sheetIndex, sheetName, rowIndex, columnIndex, value.toString());
                        break;
                    case _NONE:
                        System.out.printf("%dth sheet [%s] %dth row %dth column: none\n",
                                sheetIndex, sheetName, rowIndex, columnIndex);
                        break;

                }
            }

        }
    }

    private static final File file = new File(System.getenv("HOME") + "/Downloads/order.xlsx");

    private static final File saveFile = new File(System.getenv("HOME") + "/Downloads/order2.xlsx");

    @Test
    public void readAndShow() throws IOException {
        read(file);
    }

    @Test
    public void from() throws IOException, InvalidFormatException {
        ExcelWorkbook<ExcelSheet> book = ExcelWorkbook.from(file);
        List<ExcelSheet> sheets = book.getSheets();

        int numberOfSheets = sheets.size();
        System.out.println("numberOfSheets: " + numberOfSheets);
        for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
            ExcelSheet sheet = sheets.get(sheetIndex);
            String sheetName = sheet.getName();
            System.out.printf("%dth sheet [%s]\n",
                    sheetIndex, sheetName);
            printSheetVerbose(sheet, book);
            System.out.println();
        }

        book.writeTo(saveFile);
    }

}
