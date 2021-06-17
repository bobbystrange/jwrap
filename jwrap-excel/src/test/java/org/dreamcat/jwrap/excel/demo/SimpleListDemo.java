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
import org.dreamcat.jwrap.excel.map.SimpleSheet;

/**
 * Create by tuke on 2021/2/16
 */
public class SimpleListDemo {

    @XlsSheet(name = "Sheet via @XlsSheet")
    @Data
    static class Pojo {

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
        SimpleSheet sheet1 = new SimpleSheet("Sheet One");
        // add a styled header row to the sheet
        sheet1.addHeader(Pojo.class);
        // add many rows to the sheet
        List<Pojo> pojoList = Arrays.asList(new Pojo(), new Pojo());
        sheet1.addAll(pojoList);
        // add one row to the sheet
        sheet1.addRow(new Pojo());

        // custom bean2list, only output c & a
        sheet1.setSchemeConverter(bean -> {
            Pojo pojo = (Pojo) bean;
            return Arrays.asList(pojo.c, pojo.a);
        });

        // write data to a local excel file
        String excelFile = System.getenv("HOME") + "/Downloads/SimpleListDemo.xlsx";
        new ExcelWorkbook<>().addSheet(sheet1).writeTo(excelFile);
    }
}
