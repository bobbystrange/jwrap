package org.dreamcat.jwrap.excel.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Create by tuke on 2021/2/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelPicture {

    private byte[] data;
    private int pictureType;
    // ignore when writing
    private String contentType;

    public static ExcelPicture from(PictureData picture) {
        ExcelPicture excelPicture = new ExcelPicture();
        excelPicture.setData(picture.getData());
        excelPicture.setPictureType(picture.getPictureType());
        excelPicture.setContentType(picture.getMimeType());
        return excelPicture;
    }

    public <W extends Workbook> void fill(W workbook) {
        workbook.addPicture(data, pictureType);
    }
}
