package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.core.Pair;
import org.dreamcat.common.util.ObjectUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/1/20
 */
@Slf4j
@Component
public class EsSearchComponent {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    public Pair<List<Map<String, Object>>, Long> search(
            String index,
            QueryBuilder query,
            @Nullable List<SortBuilder<?>> sorts,
            @Nullable Integer offset, @Nullable Integer size,
            @Nullable String[] includes, @Nullable String[] excludes) {
        var searchRequest = new SearchRequest(index);
        var searchSourceBuilder = new SearchSourceBuilder()
                .fetchSource(includes, excludes)
                .trackTotalHits(true)
                .query(query);
        if (offset != null && size != null) {
            searchSourceBuilder.from(offset).size(offset);
        }
        if (ObjectUtil.isNotEmpty(sorts)) {
            for (SortBuilder<?> sort : sorts) {
                searchSourceBuilder.sort(sort);
            }
        }
        searchRequest.source(searchSourceBuilder);
        try {
            var searchResponse = restHighLevelClient.search(
                    searchRequest, RequestOptions.DEFAULT);
            var searchHits = searchResponse.getHits();
            if (searchHits == null) {
                return Pair.of(Collections.emptyList(), 0L);
            }

            var hits = Arrays.stream(searchHits.getHits())
                    .map(SearchHit::getSourceAsMap)
                    .collect(Collectors.toList());
            var total = searchHits.getTotalHits().value;
            return Pair.of(hits, total);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }
}
