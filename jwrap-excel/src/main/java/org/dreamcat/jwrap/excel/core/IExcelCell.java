package org.dreamcat.jwrap.excel.core;

import org.dreamcat.jwrap.excel.content.IExcelContent;
import org.dreamcat.jwrap.excel.style.ExcelComment;
import org.dreamcat.jwrap.excel.style.ExcelHyperLink;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelCell {

    int getRowIndex();

    int getColumnIndex();

    default int getRowSpan() {
        return 1;
    }

    default void setRowSpan(int rowSpan) {
        throw new UnsupportedOperationException();
    }

    default int getColumnSpan() {
        return 1;
    }

    default void setColumnSpan(int columnSpan) {
        throw new UnsupportedOperationException();
    }

    IExcelContent getContent();

    default int getFontIndex() {
        return -1;
    }

    default int getStyleIndex() {
        return -1;
    }

    default ExcelHyperLink getHyperLink() {
        return null;
    }

    default ExcelComment getComment() {
        return null;
    }

    default boolean hasMergedRegion() {
        return getRowSpan() > 1 || getColumnSpan() > 1;
    }

}
