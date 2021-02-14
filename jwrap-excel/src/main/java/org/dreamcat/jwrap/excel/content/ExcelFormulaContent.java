package org.dreamcat.jwrap.excel.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFormulaContent implements IExcelContent {

    private String formula;

    @Override
    public void fill(Cell cell) {
        cell.setCellFormula(formula);
    }

    @Override
    public String toString() {
        return formula;
    }
}
