package org.dreamcat.jwrap.elasticsearch.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by tuke on 2021/1/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsSearchValue {

    private String index;
    private List<EsQueryValue> query;
    private List<EsSortValue> sort;
    private Integer offset;
    private Integer limit;
    private List<String> includes;
    private List<String> excludes;
}
