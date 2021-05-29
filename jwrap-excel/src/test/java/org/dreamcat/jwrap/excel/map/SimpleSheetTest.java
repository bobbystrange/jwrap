package org.dreamcat.jwrap.excel.map;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.choose72;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;
import static org.dreamcat.jwrap.excel.util.ExcelBuilder.term;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.jwrap.excel.BaseTest;
import org.dreamcat.jwrap.excel.core.ExcelCell;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/22
 */
public class SimpleSheetTest implements BaseTest {

    @Test
    public void testSmall() {
        SimpleSheet sheet = new SimpleSheet("Sheet One");

        List<ExcelCell> cells1 = new ArrayList<>();
        cells1.add(new ExcelCell(term("A1:C2"), 0, 0, 2, 3));
        cells1.add(new ExcelCell(term("D1:D3"), 0, 3, 3, 1));
        cells1.add(new ExcelCell(term("B3:C3"), 2, 1, 1, 2));
        cells1.add(new ExcelCell(term("A3"), 2, 0));
        sheet.addRow(cells1);

        sheet.addRow(new Pojo(1, rand(), null, choose72(6)));
        sheet.addRow(new Pojo(2, rand() * (1 << 16), null, choose72(2)));

        List<ExcelCell> cells2 = new ArrayList<>();
        cells2.add(new ExcelCell(term("A6:B6"), 0, 0, 1, 2));
        cells2.add(new ExcelCell(term("A7"), 1, 0, 1, 1));
        cells2.add(new ExcelCell(term("B7:C7"), 1, 1, 1, 2));
        cells2.add(new ExcelCell(term("C6"), 0, 2, 1, 1));
        cells2.add(new ExcelCell(term("D6:D7"), 0, 3, 2, 1));
        sheet.addRow(cells2);

        sheet.addRow(new Pojo(3, rand(), null, choose72(6)));
        sheet.addRow(new Pojo(4, rand() * (1 << 16), null, choose72(2)));

        writeXlsx("SimpleListSheetTest_testSmall", sheet);
    }

    @Test
    public void test() throws Exception {
        SimpleSheet sheet = new SimpleSheet("Sheet One");
        // list1
        List<ExcelCell> cells1 = new ArrayList<>();
        cells1.add(new ExcelCell(term("A1:C2"), 0, 0, 2, 3));
        cells1.add(new ExcelCell(term("D1:D3"), 0, 3, 3, 1));
        cells1.add(new ExcelCell(term("B3:C3"), 2, 1, 1, 2));
        cells1.add(new ExcelCell(term("A3"), 2, 0));
        sheet.addRow(cells1);

        // list2
        ArrayList<Pojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            pojoList.add(new Pojo(i, rand(), null, choose26(6)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        // list3
        pojoList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            pojoList.add(new Pojo(i, rand() * (1 << 16), null, choose26(2)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        // // list4
        List<ExcelCell> cells2 = new ArrayList<>();
        cells2.add(new ExcelCell(term("A6:B6"), 0, 0, 1, 2));
        cells2.add(new ExcelCell(term("A7"), 1, 0, 1, 1));
        cells2.add(new ExcelCell(term("B7:C7"), 1, 1, 1, 2));
        cells2.add(new ExcelCell(term("C6"), 0, 2, 1, 1));
        cells2.add(new ExcelCell(term("D6:D7"), 0, 3, 2, 1));
        sheet.addRow(cells2);

        // list5
        pojoList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            pojoList.add(new Pojo(i, rand() * 8, (long) randi(1 << 16), choose72(10)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        writeXlsx("SimpleListSheetTest_test", sheet);
    }

    @Test
    public void testHuge() {
        //Thread.sleep(30_000);

        SimpleSheet sheet = new SimpleSheet("Sheet One");
        // list1
        List<ExcelCell> cells1 = new ArrayList<>();
        cells1.add(new ExcelCell(term("A1:C2"), 0, 0, 2, 3));
        cells1.add(new ExcelCell(term("D1:D3"), 0, 3, 3, 1));
        cells1.add(new ExcelCell(term("B3:C3"), 2, 1, 1, 2));
        cells1.add(new ExcelCell(term("A3"), 2, 0));
        sheet.addRow(cells1);

        // list2
        ArrayList<Pojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 20_0000; i++) {
            pojoList.add(new Pojo(i, rand(), null, choose26(6)));
        }
        sheet.addAll(pojoList);

        writeXlsxWithBigGrid("SimpleListSheetTest_testHuge", sheet);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Pojo {

        int a;
        double b;
        Long c;
        String s;
    }

}
