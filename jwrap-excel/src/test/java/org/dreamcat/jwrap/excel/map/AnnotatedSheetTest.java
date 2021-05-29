package org.dreamcat.jwrap.excel.map;

import org.dreamcat.jwrap.excel.BaseTest;
import org.dreamcat.jwrap.excel.callback.AutoWidthWriteCallback;
import org.dreamcat.jwrap.excel.callback.FitWidthWriteCallback;
import org.dreamcat.jwrap.excel.util.ExcelBuilderTest;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotatedSheetTest implements BaseTest {

    @Test
    public void testSmall() {
        // body + head + body + head + body
        AnnotatedSheet sheet1 = new AnnotatedSheet("Sheet One");
        sheet1.add(XlsMetaTest.newPojo());
        sheet1.addSheet(ExcelBuilderTest.headerSheet().finish());
        sheet1.add(XlsMetaTest.newPojo());
        sheet1.addSheet(ExcelBuilderTest.headerSheet().finish());
        sheet1.add(XlsMetaTest.newPojo());

        // head + body + head + body + head
        AnnotatedSheet sheet2 = new AnnotatedSheet("Sheet Two");
        sheet2.setAnnotationStyle(true);
        sheet2.setWriteCallback(new FitWidthWriteCallback());
        sheet2.addSheet(ExcelBuilderTest.headerSheet().finish());
        sheet2.add(XlsMetaTest.newPojo());
        sheet2.addSheet(ExcelBuilderTest.headerSheet().finish());
        sheet2.add(XlsMetaTest.newPojo());
        sheet2.addSheet(ExcelBuilderTest.headerSheet().finish());
        printSheetVerbose(sheet2);

        writeXlsx("AnnotationListSheetTest_testSmall", sheet1, sheet2);
    }

    @Test
    public void test() {
        // body + head + body + head + body
        AnnotatedSheet sheet1 = new AnnotatedSheet("Sheet One");
        sheet1.setAnnotationStyle(true);
        // sheet1.setWriteCallback(new FitWidthWriteCallback());
        sheet1.setWriteCallback(new AutoWidthWriteCallback());

        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet1.addSheet(ExcelBuilderTest.headerSheet().finish());
        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet1.addSheet(ExcelBuilderTest.headerSheet().finish());
        for (int i = 0; i < 6; i++) sheet1.add(XlsMetaTest.newPojo());

        // head + body + head + body + head
        AnnotatedSheet sheet2 = new AnnotatedSheet("Sheet Two");
        for (int i = 0; i < 6; i++) sheet2.addSheet(ExcelBuilderTest.headerSheet().finish());
        for (int i = 0; i < 6; i++) sheet2.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet2.addSheet(ExcelBuilderTest.headerSheet().finish());
        for (int i = 0; i < 6; i++) sheet2.add(XlsMetaTest.newPojo());
        for (int i = 0; i < 6; i++) sheet2.addSheet(ExcelBuilderTest.headerSheet().finish());

        writeXlsx("AnnotationListSheetTest_test", sheet1, sheet2);
    }

}
