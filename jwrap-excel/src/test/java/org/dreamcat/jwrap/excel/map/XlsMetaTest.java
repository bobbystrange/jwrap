package org.dreamcat.jwrap.excel.map;

import static org.dreamcat.common.util.BeanUtil.pretty;
import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.jwrap.excel.annotation.XlsCell;
import org.dreamcat.jwrap.excel.annotation.XlsFont;
import org.dreamcat.jwrap.excel.annotation.XlsRichStyle;
import org.dreamcat.jwrap.excel.annotation.XlsSheet;
import org.dreamcat.jwrap.excel.annotation.XlsStyle;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/25
 */
public class XlsMetaTest {

    public static Pojo newPojo() {
        return new Pojo(
                randi(10),
                Arrays.asList(rand(), rand(), rand()),
                new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3)),
                Arrays.asList(
                        new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3)),
                        new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3)),
                        new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3))
                ));
    }

    @Test
    public void test() {
        XlsMeta metadata = XlsMeta.parse(Pojo.class, true);
        System.out.println(pretty(metadata));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XlsSheet(name = "Sheet One")
    public static class Pojo {

        @XlsStyle(horizontalAlignment = HorizontalAlignment.CENTER)
        @XlsRichStyle(fgIndexedColor = IndexedColors.VIOLET)
        @XlsFont(name = "宋体", height = 24)
        int S;

        @XlsRichStyle(fgIndexedColor = IndexedColors.LEMON_CHIFFON)
        List<Double> SA;

        @XlsStyle(verticalAlignment = VerticalAlignment.CENTER)
        @XlsCell(expanded = true)
        @XlsFont(name = "黑体", height = 21, italic = true, indexedColor = IndexedColors.AQUA)
        Item V;

        @XlsRichStyle(
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
    }
}
/*
XlsMeta(
    name=Sheet One,
    defaultFont=null
    defaultStyle=null,
    cells={
        0=XlsMeta.Cell(
            fieldIndex=0, index=-1, span=1,
            expanded=false,
            font=ExcelFont(
                name=宋体, bold=false, italic=false, underline=0, strikeout=false, typeOffset=0, color=0, height=24
            ),
            style=ExcelStyle(
                horizontalAlignment=CENTER, verticalAlignment=CENTER, hidden=false, wrapText=false, locked=false, quotePrefix=false, shrinkToFit=false
            ),
            expandedType=null, expandedMeta=null
        ),
        1=XlsMeta.Cell(
            fieldIndex=1, index=-1, span=1, expanded=false, font=null, style=null, expandedType=null, expandedMeta=null
        ),
        2=XlsMeta.Cell(
            fieldIndex=2, index=-1, span=1, expanded=true, font=null, style=ExcelStyle(
                horizontalAlignment=RIGHT, verticalAlignment=CENTER, hidden=false, wrapText=false, locked=false, quotePrefix=false, shrinkToFit=false
            ),
            expandedType=class org.dreamcat.jwrap.excel.map.XlsBuilderTest$Item,
            expandedMeta=XlsMeta(
                name=item,
                defaultFont=null,
                defaultStyle=null,
                cells={
                    0=XlsMeta.Cell(
                        fieldIndex=0, index=-1, span=1, expanded=false, font=null, style=null, expandedType=null, expandedMeta=null
                    ),
                    1=XlsMeta.Cell(
                        fieldIndex=1, index=-1, span=1, expanded=false, font=null, style=null, expandedType=null, expandedMeta=null
                    )
                }
            )
        ),
        3=XlsMeta.Cell(
            fieldIndex=3, index=-1, span=1, expanded=true,
            font=ExcelFont(
                name=null, bold=true, italic=true, underline=0, strikeout=false, typeOffset=0, color=0, height=72
            ),
            style=null,
            expandedType=class org.dreamcat.jwrap.excel.map.XlsBuilderTest$Item,
            expandedMeta=XlsMeta(
                name=item, defaultFont=null, defaultStyle=null,
                cells={
                    0=XlsMeta.Cell(
                        fieldIndex=0, index=-1, span=1, expanded=false, font=null, style=null, expandedType=null, expandedMeta=null
                    ),
                    1=XlsMeta.Cell(
                        fieldIndex=1, index=-1, span=1, expanded=false, font=null, style=null, expandedType=null, expandedMeta=null
                    )
                }
            )
        )
    }
)
 */
