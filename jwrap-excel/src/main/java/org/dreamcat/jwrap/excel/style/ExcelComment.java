package org.dreamcat.jwrap.excel.style;

import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Create by tuke on 2021/2/14
 */
@Data
public class ExcelComment {

    private boolean visible;
    private String author;
    private ExcelRichString string;
    private ExcelClientAnchor clientAnchor;

    public static ExcelComment from(Comment comment) {
        ExcelComment excelComment = new ExcelComment();
        excelComment.setVisible(comment.isVisible());
        excelComment.setAuthor(comment.getAuthor());
        // string
        RichTextString string = comment.getString();
        excelComment.setString(ExcelRichString.from(string));
        // anchor
        ClientAnchor clientAnchor = comment.getClientAnchor();
        excelComment.setClientAnchor(ExcelClientAnchor.from(clientAnchor));
        return excelComment;
    }

    public void fill(Cell cell, Sheet sheet) {
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = clientAnchor.createAnchor(drawing);
        Comment comment = drawing.createCellComment(anchor);
        cell.setCellComment(comment);
    }
}
