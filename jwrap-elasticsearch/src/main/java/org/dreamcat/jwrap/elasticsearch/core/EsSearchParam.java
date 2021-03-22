package org.dreamcat.jwrap.elasticsearch.core;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.util.ObjectUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * Create by tuke on 2021/1/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsSearchParam {

    private String index;
    private List<EsQueryParam> query;
    @Nullable
    private List<EsSortParam> sort;
    @Nullable
    private Integer from;
    @Nullable
    private Integer size;
    @Nullable
    private Boolean fetchSource;
    @Nullable
    private String[] includes;
    @Nullable
    private String[] excludes;
    @Nullable
    private TimeValue keepAlive;

    public SearchRequest searchRequest() {
        QueryBuilder queryBuilder = EsQueryParam.queryBuilder(query);
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .trackTotalHits(true)
                .query(queryBuilder)
                .fetchSource(includes, excludes);
        if (fetchSource != null) {
            searchSourceBuilder.fetchSource(fetchSource);
        }
        searchRequest.source(searchSourceBuilder);
        if (from != null && size != null) {
            // there is a limit, from + size must be less than or equal to 10000 in default setting
            searchSourceBuilder.from(from).size(size);
        }
        if (ObjectUtil.isNotEmpty(sort)) {
            List<SortBuilder<?>> sortBuilders = EsSortParam.sortBuilder(sort);
            for (SortBuilder<?> sortBuilder : sortBuilders) {
                searchSourceBuilder.sort(sortBuilder);
            }
        }
        if (keepAlive != null) {
            searchRequest.scroll(keepAlive);
        }
        return searchRequest;
    }
}
