package org.dreamcat.jwrap.excel.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

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
        cell.setCellType(CellType.FORMULA);
        cell.setCellValue(formula);
    }

    @Override
    public String toString() {
        return formula;
    }
}
