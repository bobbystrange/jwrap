package org.dreamcat.jwrap.excel.benchmark;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;
import static org.dreamcat.jwrap.excel.map.XlsMetaTest.Item;
import static org.dreamcat.jwrap.excel.map.XlsMetaTest.Pojo;
import static org.dreamcat.jwrap.excel.util.ExcelBuilder.term;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.dreamcat.common.core.Timeit;
import org.dreamcat.common.util.BeanUtil;
import org.dreamcat.jwrap.excel.callback.FitWidthWriteCallback;
import org.dreamcat.jwrap.excel.core.ExcelCell;
import org.dreamcat.jwrap.excel.core.ExcelSheet;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.map.AnnotationListSheet;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/27
 */
@SuppressWarnings("unchecked")
public class AnnotationListSheetSpeedTest {

    // xmx128m, max 88%
    @Test
    public void testByCopy() throws Exception {
        byCopy(newPojoList(64));
        byCopy(newPojoList(1024));
        byCopy(newPojoList(4096));
    }

    // xmx128m, max 70%
    @Test
    public void testByReuse() throws Exception {
        byReuse(newPojoList(64));
        byReuse(newPojoList(1024));
        byReuse(newPojoList(4096));
    }

    /**
     * copy 	 reuse
     * 00001 	 -39.148% 21.376ms	29.745ms
     * 00005 	 -6.393% 23.478ms	24.979ms
     * 00025 	  7.800% 31.924ms	29.434ms
     * 00125 	 -26.866% 77.005ms	97.692ms
     * 00625 	 11.530% 1150.030ms	1017.429ms
     * 03125 	  1.062% 20580.130ms	20361.647ms
     */
    @Test
    public void test() {
        System.out.println("  \t\t\t\t copy \t reuse");
        for (int i = 1; i <= 4096; i *= 5) {
            int size = i;
            long[] ts = Timeit.ofActions()
                    .addUnaryAction(() -> newPojoList(size), this::byCopy)
                    .addUnaryAction(() -> newPojoList(size), this::byReuse)
                    .count(3).skip(1).run();
            String s = Arrays.stream(ts).mapToObj(it -> String.format("%6.3fms", it / 1000_000.))
                    .collect(Collectors.joining("\t"));
            System.out.printf("%05d \t %6.3f%% %s\n", i, (1 - ((0.0 + ts[1]) / ts[0])) * 100, s);
        }
    }

    private void byReuse(List<Pojo> pojoList) throws IOException {
        AnnotationListSheet sheet = new AnnotationListSheet("Sheet One");
        sheet.setAnnotationStyle(false);
        sheet.addAll(pojoList);
        sheet.setWriteCallback(new FitWidthWriteCallback());
        new ExcelWorkbook<>()
                .add(sheet)
                .writeToWithBigGrid("/Users/tuke/Downloads/book2.xlsx");
    }

    public void byCopy(List<Pojo> pojoList) throws IOException {
        ExcelSheet sheet = new ExcelSheet("Sheet One");
        int ri = 0;
        for (Pojo pojo : pojoList) {
            List<Object> values = BeanUtil.toList(pojo);
            Integer s = (Integer) values.get(0);
            List<Double> sa = (List<Double>) values.get(1);
            Item v = (Item) values.get(2);
            List<Item> va = (List<Item>) values.get(3);

            int maxRowSpan = sa.size();
            maxRowSpan = Math.max(maxRowSpan, va.size());

            sheet.getCells().add(new ExcelCell(term(s), ri, 0, maxRowSpan, 1));
            int k = ri;
            for (Double n : sa) {
                sheet.getCells().add(new ExcelCell(term(n), k++, 1, 1, 1));
            }

            List<Object> vi = BeanUtil.toList(v);
            sheet.getCells().add(new ExcelCell(term(vi.get(0)), ri, 2, maxRowSpan, 1));
            sheet.getCells().add(new ExcelCell(term(vi.get(1)), ri, 3, maxRowSpan, 1));

            k = ri;
            for (Item a : va) {
                List<Object> vai = BeanUtil.toList(a);
                sheet.getCells().add(new ExcelCell(term(vai.get(0)), k, 4, 1, 1));
                sheet.getCells().add(new ExcelCell(term(vai.get(1)), k++, 5, 1, 1));
            }

            ri += maxRowSpan;
        }

        sheet.setWriteCallback(new FitWidthWriteCallback());
        new ExcelWorkbook<>()
                .add(sheet)
                .writeToWithBigGrid("/Users/tuke/Downloads/book1.xlsx");
    }

    private List<Pojo> newPojoList(int size) {
        List<Pojo> pojoList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Pojo pojo = new Pojo(
                    randi(10),
                    Arrays.asList(rand(), rand(), rand()),
                    new Item((long) (randi(1 << 16)), choose26(3)),
                    Arrays.asList(
                            new Item((long) (randi(1 << 16)), choose26(3)),
                            new Item((long) (randi(1 << 16)), choose26(3)),
                            new Item((long) (randi(1 << 16)), choose26(3))
                    ));
            pojoList.add(pojo);
        }
        return pojoList;
    }

}
