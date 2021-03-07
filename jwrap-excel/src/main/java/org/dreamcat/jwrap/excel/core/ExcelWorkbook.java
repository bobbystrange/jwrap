package org.dreamcat.jwrap.excel.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
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
import org.dreamcat.jwrap.excel.util.InternalUtil;

/**
 * Create by tuke on 2020/7/21
 */
public class ExcelWorkbook<T extends IExcelSheet> implements IExcelWorkbook<T> {

    @Getter
    final List<ExcelPicture> pictures;
    @Getter
    final List<T> sheets;
    final Map<ExcelFont, Font> fonts;
    final Map<Font, ExcelFont> reversedFonts;
    final Map<ExcelStyle, CellStyle> styles;
    final Map<CellStyle, ExcelStyle> reversedStyles;

    public ExcelWorkbook() {
        this.sheets = new ArrayList<>();
        this.fonts = new HashMap<>();
        this.reversedFonts = new HashMap<>();
        this.styles = new HashMap<>();
        this.reversedStyles = new HashMap<>();
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
            excelWorkbook.fonts.put(excelFont, font);
        }
        // cell style
        int cellStyleNum = workbook.getNumCellStyles();
        for (int i = 0; i < cellStyleNum; i++) {
            CellStyle cellStyle = workbook.getCellStyleAt(i);
            Font font = InternalUtil.getFont(cellStyle.getFontIndex(), workbook);
            ExcelStyle excelStyle = ExcelStyle.from(cellStyle, font);
            excelWorkbook.styles.put(excelStyle, cellStyle);
        }
        // sheet
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            excelWorkbook.sheets.add(ExcelSheet.from(sheet, excelWorkbook));
        }
        // picture
        List<? extends PictureData> pictures = workbook.getAllPictures();
        for (PictureData picture : pictures) {
            excelWorkbook.pictures.add(ExcelPicture.from(picture));
        }
        return excelWorkbook;
    }

    @Override
    public CellStyle makeCellStyle(IExcelCell excelCell, Workbook workbook) {
        ExcelStyle excelStyle = excelCell.getStyle();
        if (excelStyle == null) return null;

        CellStyle style = styles.get(excelStyle);
        if (style != null) return null;
        style = workbook.createCellStyle();
        styles.put(excelStyle, style);
        reversedStyles.put(style, excelStyle);

        Font font = null;
        ExcelFont excelFont = excelStyle.getFont();
        if (excelFont != null) {
            font = fonts.get(excelFont);
            if (font == null) {
                font = workbook.createFont();
                fonts.put(excelFont, font);
                reversedFonts.put(font, excelFont);
            }
        }
        excelStyle.fill(style, font);
        return style;
    }
}
