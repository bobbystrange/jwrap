package org.dreamcat.jwrap.excel.style;

import lombok.Data;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Drawing;

/**
 * Create by tuke on 2021/2/14
 */
@Data
public class ExcelClientAnchor {

    private AnchorType anchorType;
    private int dx1;
    private int dy1;
    private int dx2;
    private int dy2;
    private int col1;
    private int row1;
    private int col2;
    private int row2;

    public static ExcelClientAnchor from(ClientAnchor clientAnchor) {
        ExcelClientAnchor excelClientAnchor = new ExcelClientAnchor();
        excelClientAnchor.setAnchorType(clientAnchor.getAnchorType());
        excelClientAnchor.setDx1(clientAnchor.getDx1());
        excelClientAnchor.setDy1(clientAnchor.getDy1());
        excelClientAnchor.setDx2(clientAnchor.getDx2());
        excelClientAnchor.setDy2(clientAnchor.getDy2());
        excelClientAnchor.setCol1(clientAnchor.getCol1());
        excelClientAnchor.setRow1(clientAnchor.getRow1());
        excelClientAnchor.setCol2(clientAnchor.getCol2());
        excelClientAnchor.setRow2(clientAnchor.getRow2());
        return excelClientAnchor;
    }

    public ClientAnchor createAnchor(Drawing<?> drawing) {
        ClientAnchor anchor = drawing.createAnchor(dx1, dy1, dx1, dy2, col1, row1, col2, row2);
        anchor.setAnchorType(anchorType);
        return anchor;
    }
}
