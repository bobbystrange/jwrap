package org.dreamcat.jwrap.excel.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.dreamcat.jwrap.excel.annotation.XlsCell;
import org.dreamcat.jwrap.excel.annotation.XlsHeader;
import org.dreamcat.jwrap.excel.annotation.XlsSheet;
import org.dreamcat.jwrap.excel.annotation.XlsStyle;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.core.IExcelSheet;
import org.dreamcat.jwrap.excel.map.SimpleListSheet;

/**
 * Create by tuke on 2021/2/16
 */
public class SimpleListDemo {

    @XlsSheet(name = "Sheet via @XlsSheet")
    @Data
    private static class Pojo {

        @XlsHeader(
                header = "Cell A",
                style = @XlsStyle(fgIndexedColor = IndexedColors.AQUA)
        )
        int a;
        @XlsHeader(header = "Cell B")
        Double b = Math.random();
        @XlsHeader(header = "Cell C")
        String c = UUID.randomUUID().toString()
                .replaceAll("-", "").substring(0, 8);
    }

    public static void main(String[] args) throws IOException {
        // build a empty sheet called "Sheet One"
        SimpleListSheet sheet = new SimpleListSheet("Sheet One");
        // add a header row to the sheet
        sheet.addHeader(Pojo.class);
        // build some data
        List<Pojo> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new Pojo());
        }
        // add many rows to the sheet
        sheet.addAll(list);
        // add one row to the sheet
        sheet.add(new Pojo());

        // build a empty workbook
        ExcelWorkbook<IExcelSheet> book = new ExcelWorkbook<>();
        // attach sheet to workbook
        book.addSheet(sheet);
        // write data to local file
        File baseDir = new File(System.getenv("HOME"), "Downloads");
        book.writeTo(new File(baseDir, "SimpleListDemo.xlsx"));
    }
}
