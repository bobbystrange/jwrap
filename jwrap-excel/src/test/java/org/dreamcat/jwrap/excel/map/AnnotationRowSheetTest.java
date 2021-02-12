package org.dreamcat.jwrap.excel.map;

import static org.dreamcat.common.util.RandomUtil.choose10;
import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.choose72;
import static org.dreamcat.common.util.RandomUtil.randi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dreamcat.common.x.asm.BeanMapUtil;
import org.dreamcat.jwrap.excel.core.ExcelWorkbook;
import org.dreamcat.jwrap.excel.core.IExcelCell;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotationRowSheetTest {

    @Test
    public void test() throws Exception {
        AnnotationRowSheet sheet = new AnnotationRowSheet(XlsMetaTest.newPojo());
        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    cell.getFont(), cell.getStyle()
            );
        }

        new ExcelWorkbook<>()
                .add(sheet)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Test
    public void testDynamicColumn() throws Exception {
        DynamicPojo pojo = newDynamicPojo();
        System.out.println(pojo);

        AnnotationRowSheet sheet = new AnnotationRowSheet(pojo);

        new ExcelWorkbook<>()
                .add(sheet)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DynamicPojo extends XlsMetaTest.Pojo {

        private Map<String, String> map;
        private List<Map<String, String>> mapList;

    }

    public static DynamicPojo newDynamicPojo() {
        DynamicPojo pojo = new DynamicPojo();
        BeanMapUtil.copy(XlsMetaTest.newPojo(), pojo);

        Map<String, String> map = new HashMap<>();
        map.put("a", "map-a-" + choose10(12));
        map.put("b", "map-b-" + choose36(randi(2, 6)));
        map.put("c", "map-c-" + choose72(randi(3, 4)));
        pojo.setMap(map);

        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> m = new HashMap<>();
            m.put("a", "mapList-a-" + choose10(12));
            m.put("b", "mapList-b-" + choose36(randi(2, 6)));
            m.put("c", "mapList-c-" + choose72(randi(3, 4)));
            mapList.add(m);
        }
        pojo.setMapList(mapList);
        return pojo;
    }
}
