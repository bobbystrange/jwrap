package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.core.Pair;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.jwrap.elasticsearch.common.EsSearchValue;
import org.dreamcat.jwrap.elasticsearch.util.EsQueryUtil;
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
            EsSearchValue search) {
        var query = EsQueryUtil.boolMust(search.getQuery());
        var sort = EsQueryUtil.sort(search.getSort());
        return search(search.getIndex(), query, sort,
                search.getOffset(), search.getLimit(), search.getIncludes(), search.getExcludes());
    }

    public Pair<List<Map<String, Object>>, Long> search(
            String index,
            QueryBuilder query,
            @Nullable List<SortBuilder<?>> sort,
            @Nullable Integer offset, @Nullable Integer limit,
            @Nullable List<String> includes, @Nullable List<String> excludes) {
        if (log.isDebugEnabled()) {
            log.debug("searching es: index={}, query={}, sort={}",
                    index, query, sort);
        }

        var searchRequest = new SearchRequest(index);
        var searchSourceBuilder = new SearchSourceBuilder()
                .trackTotalHits(true)
                .query(query);

        var includesArray = Optional.ofNullable(includes)
                .map(it -> it.toArray(String[]::new)).orElse(null);
        var excludesArray = Optional.ofNullable(excludes)
                .map(it -> it.toArray(String[]::new)).orElse(null);
        searchSourceBuilder.fetchSource(includesArray, excludesArray);

        if (offset != null && limit != null) {
            searchSourceBuilder.from(offset).size(limit);
        }
        if (ObjectUtil.isNotEmpty(sort)) {
            for (var sortBuilder : sort) {
                searchSourceBuilder.sort(sortBuilder);
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
