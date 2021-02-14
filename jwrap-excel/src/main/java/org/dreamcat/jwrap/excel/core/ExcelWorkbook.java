package org.dreamcat.jwrap.excel.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.jwrap.excel.content.ExcelPicture;
import org.dreamcat.jwrap.excel.style.ExcelFont;
import org.dreamcat.jwrap.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelWorkbook<T extends IExcelSheet> implements IExcelWorkbook<T> {

    private final List<T> sheets;
    private final List<ExcelFont> fonts;
    private final List<ExcelStyle> styles;
    private final List<ExcelPicture> pictures;

    public ExcelWorkbook() {
        this.sheets = new ArrayList<>();
        this.fonts = new ArrayList<>();
        this.styles = new ArrayList<>();
        this.pictures = new ArrayList<>();
    }

    public static ExcelWorkbook<ExcelSheet> from(File file)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> fromBigGrid(File file)
            throws IOException, InvalidFormatException {
        try (Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook(file))) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> from2003(File file) throws IOException {
        try (Workbook workbook = new HSSFWorkbook(new POIFSFileSystem(file, true))) {
            return from(workbook);
        }
    }

    public static ExcelWorkbook<ExcelSheet> from(Workbook workbook) {
        ExcelWorkbook<ExcelSheet> excelWorkbook = new ExcelWorkbook<>();
        // font
        int fontNum = workbook.getNumberOfFonts();
        for (int i = 0; i < fontNum; i++) {
            Font font = workbook.getFontAt(i);
            ExcelFont excelFont = ExcelFont.from(font);
            excelWorkbook.fonts.add(excelFont);
        }
        // cell style
        int cellStyleNum = workbook.getNumCellStyles();
        for (int i = 0; i < cellStyleNum; i++) {
            CellStyle cellStyle = workbook.getCellStyleAt(i);
            ExcelStyle excelStyle = ExcelStyle.from(cellStyle);
            excelWorkbook.styles.add(excelStyle);
        }
        // sheet
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            excelWorkbook.sheets.add(ExcelSheet.from(sheet));
        }
        // picture
        List<? extends PictureData> pictures = workbook.getAllPictures();
        for (PictureData picture : pictures) {
            excelWorkbook.pictures.add(ExcelPicture.from(picture));
        }
        return excelWorkbook;
    }
}
