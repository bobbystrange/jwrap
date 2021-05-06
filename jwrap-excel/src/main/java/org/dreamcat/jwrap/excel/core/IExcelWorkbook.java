package org.dreamcat.jwrap.excel.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.jwrap.excel.content.ExcelPicture;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelWorkbook<T extends IExcelSheet> extends Iterable<T> {

    @Override
    default Iterator<T> iterator() {
        return getSheets().iterator();
    }

    List<T> getSheets();

    default IExcelWorkbook<T> addSheet(T sheet) {
        getSheets().add(sheet);
        return this;
    }

    default IExcelWorkbook<T> addSheets(Iterable<T> sheets) {
        for (T sheet : sheets) {
            addSheet(sheet);
        }
        return this;
    }

    List<ExcelPicture> getPictures();

    default IExcelWorkbook<T> addPicture(ExcelPicture picture) {
        getPictures().add(picture);
        return this;
    }

    default IExcelWorkbook<T> addPictures(Collection<ExcelPicture> pictures) {
        getPictures().addAll(pictures);
        return this;
    }

    /**
     * make a {@link CellStyle} or return a existed one
     *
     * @param excelCell the cell to provide the cell style
     * @param workbook  the workbook to store the cell style
     * @return the cell style for the cell
     */
    CellStyle makeCellStyle(IExcelCell excelCell, Workbook workbook);

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    default XSSFWorkbook toWorkbook() {
        return toWorkbook(new XSSFWorkbook());
    }

    default SXSSFWorkbook toWorkbookWithBigGrid() {
        return toWorkbook(new SXSSFWorkbook());
    }

    default HSSFWorkbook toWorkbook2003() {
        return toWorkbook(new HSSFWorkbook());
    }

    default <W extends Workbook> W toWorkbook(W workbook) {
        // sheet
        int sheetIndex = 0;
        for (T excelSheet : this) {
            Sheet sheet = workbook.createSheet(excelSheet.getName());
            excelSheet.fill(sheet, sheetIndex++, this);
        }
        // picture
        List<ExcelPicture> pictures = getPictures();
        for (ExcelPicture picture : pictures) {
            picture.fill(workbook);
        }
        return workbook;
    }

    default void writeTo(String newFile) throws IOException {
        writeTo(new File(newFile));
    }

    default void writeTo(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeTo(ostream);
        }
    }

    default void writeTo(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbook()) {
            workbook.write(output);
        }
    }

    default void writeToWithBigGrid(String newFile) throws IOException {
        writeToWithBigGrid(new File(newFile));
    }

    default void writeToWithBigGrid(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeToWithBigGrid(ostream);
        }
    }

    default void writeToWithBigGrid(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbookWithBigGrid()) {
            workbook.write(output);
        }
    }

    default void writeTo2003(String newFile) throws IOException {
        writeTo2003(new File(newFile));
    }

    default void writeTo2003(File newFile) throws IOException {
        try (FileOutputStream ostream = new FileOutputStream(newFile)) {
            writeTo2003(ostream);
        }
    }

    default void writeTo2003(OutputStream output) throws IOException {
        try (Workbook workbook = toWorkbook2003()) {
            workbook.write(output);
        }
    }

    default byte[] toByteArray() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbook()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

    default byte[] toByteArrayWithBigGrid() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbookWithBigGrid()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

    default byte[] toByteArray2003() throws IOException {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                Workbook workbook = toWorkbook2003()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        }
    }

}
