package org.dreamcat.jwrap.elasticsearch.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Create by tuke on 2021/1/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsSortValue {

    private String name;
    private SortOrder order;
}
