package org.dreamcat.jwrap.elasticsearch.core;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Create by tuke on 2021/1/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsSortParam {

    private String name;
    private SortOrder order;

    public SortBuilder<?> sortBuilder() {
        return SortBuilders.fieldSort(name).order(order);
    }

    public static List<SortBuilder<?>> sortBuilder(List<EsSortParam> sort) {
        return sort.stream()
                .map(EsSortParam::sortBuilder)
                .collect(Collectors.toList());
    }
}
