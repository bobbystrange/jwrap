package org.dreamcat.jwrap.excel.opencsv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.Reader;
import java.util.List;

/**
 * Create by tuke on 2020/2/12
 */
public class CsvFile {

    public static <T> List<T> parseCSV(Reader reader, Class<T> clazz) {

        HeaderColumnNameMappingStrategy<T> mappingStrategy =

                new HeaderColumnNameMappingStrategy<>();

        mappingStrategy.setType(clazz);

        CsvToBean<T> csv = new CsvToBeanBuilder<T>(reader)
                .withMappingStrategy(mappingStrategy)
                .build();
        return csv.parse();
    }
}
