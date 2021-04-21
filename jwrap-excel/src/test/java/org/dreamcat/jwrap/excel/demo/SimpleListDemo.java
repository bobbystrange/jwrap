package org.dreamcat.jwrap.excel.demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.dreamcat.jwrap.excel.annotation.XlsHeader;
import org.dreamcat.jwrap.excel.annotation.XlsSheet;
import org.dreamcat.jwrap.excel.annotation.XlsStyle;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
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
        // add a styled header row to the sheet
        sheet.addHeader(Pojo.class);
        // add many rows to the sheet
        List<Pojo> pojoList = Arrays.asList(new Pojo(), new Pojo());
        sheet.addAll(pojoList);
        // add one row to the sheet
        sheet.add(new Pojo());

        // write data to a local excel file
        String excelFile = System.getenv("HOME") + "/Downloads/SimpleListDemo.xlsx";
        new ExcelWorkbook<>().addSheet(sheet).writeTo(excelFile);
    }
}
