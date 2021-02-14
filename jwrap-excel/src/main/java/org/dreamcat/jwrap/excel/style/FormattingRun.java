package org.dreamcat.jwrap.excel.style;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by tuke on 2021/2/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormattingRun {

    private String string;
    private short fontIndex;

    public FormattingRun(String string) {
        this.string = string;
    }
}
