package org.dreamcat.jwrap.excel.map;

import org.dreamcat.jwrap.excel.callback.AutoWidthWriteCallback;
import org.dreamcat.jwrap.excel.callback.FitWidthWriteCallback;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.util.ExcelBuilderTest;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotationListSheetTest {

    @Test
    public void testSmall() throws Exception {
        // body + head + body + head + body
        AnnotationListSheet sheet1 = new AnnotationListSheet("Sheet One");
        sheet1.add(XlsMetaTest.newPojo());
        sheet1.add(ExcelBuilderTest.headerSheet());
        sheet1.add(XlsMetaTest.newPojo());
        sheet1.add(ExcelBuilderTest.headerSheet());
        sheet1.add(XlsMetaTest.newPojo());

        // head + body + head + body + head
        AnnotationListSheet sheet2 = new AnnotationListSheet("Sheet Two");
        sheet2.setAnnotationStyle(true);
        sheet2.setWriteCallback(new FitWidthWriteCallback());
        sheet2.add(ExcelBuilderTest.headerSheet());
        sheet2.add(XlsMetaTest.newPojo());
        sheet2.add(ExcelBuilderTest.headerSheet());
        sheet2.add(XlsMetaTest.newPojo());
        sheet2.add(ExcelBuilderTest.headerSheet());

        new ExcelWorkbook<>()
                .add(sheet1)
                .add(sheet2)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Test
    public void test() throws Exception {
        // body + head + body + head + body
        AnnotationListSheet sheet1 = new AnnotationListSheet("Sheet One");
        sheet1.setAnnotationStyle(true);
        // sheet1.setWriteCallback(new FitWidthWriteCallback());
        sheet1.setWriteCallback(new AutoWidthWriteCallback());

        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet1.add(ExcelBuilderTest.headerSheet());
        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet1.add(ExcelBuilderTest.headerSheet());
        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());

        // head + body + head + body + head
        AnnotationListSheet sheet2 = new AnnotationListSheet("Sheet Two");
        for (int i = 0; i < 6; i++) sheet2.add(ExcelBuilderTest.headerSheet());
        for (int i = 0; i < 6; i++) sheet2.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet2.add(ExcelBuilderTest.headerSheet());
        for (int i = 0; i < 6; i++) sheet2.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet2.add(ExcelBuilderTest.headerSheet());

        new ExcelWorkbook<>()
                .add(sheet1)
                .add(sheet2)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

}
