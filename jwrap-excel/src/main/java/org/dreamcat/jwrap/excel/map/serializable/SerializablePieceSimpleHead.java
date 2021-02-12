package org.dreamcat.jwrap.excel.map.serializable;

import lombok.Data;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Create by tuke on 2021/2/5
 */
@Data
public class SerializablePieceSimpleHead {

    private String value;
    // font
    private String fontName;
    private boolean fontBold;
    private boolean fontItalic;
    // style
    private short bgColor;
    private short fgColor;

}
