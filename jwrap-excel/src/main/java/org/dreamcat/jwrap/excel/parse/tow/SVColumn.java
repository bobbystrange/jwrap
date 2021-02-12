package org.dreamcat.jwrap.excel.parse.tow;

import java.util.Map;
import lombok.Data;

/**
 * Create by tuke on 2020/8/27
 */
@Data
public class SVColumn<S> {

    public S scalar;
    public Map<String, String> map;
}
