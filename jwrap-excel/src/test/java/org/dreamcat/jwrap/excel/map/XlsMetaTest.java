package org.dreamcat.jwrap.excel.map;

import static org.dreamcat.common.util.BeanUtil.pretty;
import static org.dreamcat.common.util.RandomUtil.choose10;
import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.choose72;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.util.ArrayUtil;
import org.dreamcat.common.x.asm.BeanMapUtil;
import org.dreamcat.jwrap.excel.BaseTest;
import org.dreamcat.jwrap.excel.annotation.XlsCell;
import org.dreamcat.jwrap.excel.annotation.XlsFont;
import org.dreamcat.jwrap.excel.annotation.XlsSheet;
import org.dreamcat.jwrap.excel.annotation.XlsStyle;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/25
 */
public class XlsMetaTest implements BaseTest {

    public static Pojo newPojo() {
        Pojo pojo = new Pojo();
        pojo.setS(randi(10));
        pojo.setSA(Arrays.stream(ArrayUtil.rangeOf(1, randi(2, 6)))
                .mapToObj(it -> rand())
                .collect(Collectors.toList()));
        pojo.setV(newItem());
        pojo.setVA(newItems());
        return pojo;
    }

    public static DynamicPojo newDynamicPojo() {
        return newDynamicPojo(newPojo());
    }

    public static DynamicPojo newDynamicPojo(Pojo pojo) {
        DynamicPojo dynamicPojo = new DynamicPojo();
        BeanMapUtil.copy(pojo, dynamicPojo);

        Map<String, String> map = new HashMap<>();
        map.put("a", "map-a-" + choose10(12));
        map.put("b", "map-b-" + choose36(randi(2, 6)));
        map.put("c", "map-c-" + choose72(randi(3, 4)));
        dynamicPojo.setD(map);

        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> m = new HashMap<>();
            m.put("a", "map-list-a-" + choose10(12));
            m.put("b", "map-list-b-" + choose36(randi(2, 6)));
            m.put("c", "map-list-c-" + choose72(randi(3, 4)));
            mapList.add(m);
        }
        dynamicPojo.setDA(mapList);
        return dynamicPojo;
    }

    public static List<String> newStrings() {
        return Arrays.stream(ArrayUtil.rangeOf(0, 3))
                .mapToObj(it -> "SA")
                .collect(Collectors.toList());
    }

    public static List<Item> newItems() {
        return Arrays.stream(ArrayUtil.rangeOf(0, 3))
                .mapToObj(it -> new XlsMetaTest.Item(
                        (long) (randi(1 << 8)),
                        "VA"))
                .collect(Collectors.toList());
    }

    public static Item newItem() {
        return new XlsMetaTest.Item((long) (randi(1 << 8)), "V");
    }

    public static List<Map<String, String>> newMaps() {
        return Arrays.stream(ArrayUtil.rangeOf(0, 3))
                .mapToObj(it -> {
                    Map<String, String> m = new HashMap<>();
                    for (int i = 0; i < randi(3, 7); i++) {
                        m.put("$", "DA");
                    }
                    return m;
                })
                .collect(Collectors.toList());
    }

    public static Map<String, String> newMap() {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < randi(3, 7); i++) {
            m.put("$", "D");
        }
        return m;
    }

    @Test
    public void test() {
        XlsMeta metadata = XlsMeta.parse(Pojo.class, true);
        assert metadata != null;
        printSheetVerbose(metadata);

        writeXlsx("XlsMetaTest_test", metadata);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XlsSheet(name = "Pojo")
    public static class Pojo {

        @XlsStyle(horizontalAlignment = HorizontalAlignment.CENTER,
                fgIndexedColor = IndexedColors.RED)
        @XlsFont(name = "宋体", height = 24)
        int S;

        @XlsStyle(fgIndexedColor = IndexedColors.LEMON_CHIFFON,
                bgIndexedColor = IndexedColors.GREEN,
                fillPattern = FillPatternType.ALT_BARS)
        List<Double> SA;

        @XlsStyle(verticalAlignment = VerticalAlignment.CENTER)
        @XlsCell(expanded = true, subheader = true)
        @XlsFont(name = "黑体", height = 21, italic = true, indexedColor = IndexedColors.AQUA)
        Item V;

        @XlsStyle(
                fgIndexedColor = IndexedColors.ROSE,
                borderBottom = BorderStyle.DASH_DOT_DOT,
                borderLeft = BorderStyle.THICK)
        @XlsFont(name = "微软雅黑", height = 16, bold = true, italic = true)
        @XlsCell(expanded = true, expandedType = Item.class)
        List<Item> VA;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XlsSheet(name = "item")
    public static class Item {

        Long r1;
        String r2;

        @Override
        public String toString() {
            return r2;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @XlsSheet(name = "DynamicPojo")
    public static class DynamicPojo extends Pojo {

        private Map<String, String> D;
        private List<Map<String, String>> DA;

    }
}
