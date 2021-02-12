package org.dreamcat.jwrap.excel.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.function.UnaryOperator;

/**
 * Create by tuke on 2020/7/28
 * <p>
 * Common Format and MIME Type for Comma-Separated Values (CSV) Files
 * https://tools.ietf.org/html/rfc4180
 */
public interface ICsvWorkbook extends Iterable<Iterable<String>> {

    // 1. Each record is located on a separate line, delimited by a line break (CRLF)
    // 2. The last record in the file may or may not have an ending line break
    static String escapeCsv(String value) {
        if (value == null) return "";
        int size = value.length();
        // 5 = 2 "", and expect extra at most 2 " in the string
        StringBuilder sb = new StringBuilder(size + 4);
        sb.append("\"");
        boolean enclosed = false;
        for (int i = 0; i < size; i++) {
            char c = value.charAt(i);
            sb.append(c);
            // If double-quotes are used to enclose fields, then a double-quote
            // appearing inside a field must be escaped by preceding it with
            // another double quote
            if (c == '"') sb.append(c);
            if (!enclosed && (c == ',' || c == '\n' || c == '\r')) enclosed = true;
        }

        if (enclosed) {
            sb.append("\"");
            return sb.toString();
        } else {
            return value;
        }
    }

    static String escapeTsv(String value) {
        return value;
    }

    default void writeToCsv(String newFile) throws IOException {
        writeToCsv(new File(newFile));
    }

    default void writeToCsv(File newFile) throws IOException {
        try (FileWriter writer = new FileWriter(newFile)) {
            writeToCsv(writer);
        }
    }

    default void writeToTsv(String newFile) throws IOException {
        writeToTsv(new File(newFile));
    }

    default void writeToTsv(File newFile) throws IOException {
        try (FileWriter writer = new FileWriter(newFile)) {
            writeToTsv(writer);
        }
    }

    default void writeToCsv(Writer writer) throws IOException {
        writeTo(writer, ",", ICsvWorkbook::escapeCsv);
    }

    default void writeToTsv(Writer writer) throws IOException {
        writeTo(writer, "\t", ICsvWorkbook::escapeTsv);
    }

    default void writeTo(Writer writer, String separator, UnaryOperator<String> escaping)
            throws IOException {
        for (Iterable<String> row : this) {
            StringBuilder rowString = new StringBuilder();
            Iterator<String> iterator = row.iterator();
            while (iterator.hasNext()) {
                String value = iterator.next();
                rowString.append(escaping.apply(value));
                if (iterator.hasNext()) {
                    rowString.append(separator);
                }
            }

            writer.write(rowString.toString());
            writer.write("\r\n");
        }
    }

}
