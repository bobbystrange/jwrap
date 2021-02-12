package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.core.Pair;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.dreamcat.jwrap.elasticsearch.core.EsQueryValue;
import org.dreamcat.jwrap.elasticsearch.core.EsSortValue;
import org.dreamcat.jwrap.elasticsearch.util.EsQueryUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilder;

/**
 * Create by tuke on 2021/1/20
 */
@Slf4j
@SuppressWarnings({"unchecked"})
@RequiredArgsConstructor
public class EsSearchComponent {

    private final RestHighLevelClient restHighLevelClient;

    public boolean exists(String index, String id) {
        GetRequest request = new GetRequest(index).id(id);

        try {
            return restHighLevelClient.exists(
                    request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public Map<String, Object> get(String index, String id) {
        return (Map<String, Object>) get(index, id, Map.class);
    }

    public <T> T get(String index, String id, Class<T> clazz) {
        GetRequest request = new GetRequest(index, id);
        try {
            GetResponse response = restHighLevelClient.get(
                    request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("get from index {}, result: {}", index, response);
            }
            return fromGetResponse(response, clazz);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public List<Map<String, Object>> mget(
            String index, Collection<String> ids) {
        return mget(index, ids, null, null);
    }

    public List<Map<String, Object>> mget(
            String index, Collection<String> ids,
            @Nullable String[] includes, @Nullable String[] excludes) {
        MultiGetResponse response = multiGet(index, ids, includes, excludes);
        List<Map<String, Object>> list = new ArrayList<>();
        for (MultiGetItemResponse itemResponse : response) {
            GetResponse getResponse = itemResponse.getResponse();
            if (getResponse == null) continue;
            Map<String, Object> item = fromGetResponse(getResponse, HashMap.class);
            list.add(item);
        }
        return list;
    }

    public <T> List<T> mget(
            String index, Collection<String> ids, Class<T> clazz) {
        return mget(index, ids, null, null, clazz);
    }

    public <T> List<T> mget(
            String index, Collection<String> ids,
            @Nullable String[] includes, @Nullable String[] excludes,
            Class<T> clazz) {
        MultiGetResponse response = multiGet(index, ids, includes, excludes);
        List<T> list = new ArrayList<>();
        for (MultiGetItemResponse itemResponse : response) {
            GetResponse getResponse = itemResponse.getResponse();
            if (getResponse == null) continue;
            T item = fromGetResponse(getResponse, clazz);
            list.add(item);
        }
        return list;
    }

    private MultiGetResponse multiGet(
            String index, Collection<String> ids,
            @Nullable String[] includes, @Nullable String[] excludes) {
        MultiGetRequest request = new MultiGetRequest();
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        for (String id : ids) {
            MultiGetRequest.Item item = new MultiGetRequest.Item(index, id);
            item.fetchSourceContext(fetchSourceContext);
            request.add(item);
        }
        try {
            return restHighLevelClient.mget(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    private static <T> T fromGetResponse(GetResponse response, Class<T> clazz) {
        if (!response.isExists()) {
            return null;
        }
        String json = response.getSourceAsString();
        return JacksonUtil.fromJson(json, clazz);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public Pair<List<Map<String, Object>>, Long> search(
            String index,
            List<EsQueryValue> query,
            @Nullable List<EsSortValue> sort,
            @Nullable Integer offset, @Nullable Integer limit,
            @Nullable String[] includes, @Nullable String[] excludes) {
        return search(index, EsQueryUtil.boolMust(query), EsQueryUtil.sort(sort),
                offset, limit, includes, excludes);
    }

    public Pair<List<Map<String, Object>>, Long> search(
            String index,
            QueryBuilder query,
            @Nullable List<SortBuilder<?>> sort,
            @Nullable Integer offset, @Nullable Integer limit,
            @Nullable String[] includes, @Nullable String[] excludes) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .trackTotalHits(true)
                .query(query)
                .fetchSource(includes, excludes);
        ;
        searchRequest.source(searchSourceBuilder);

        if (offset != null && limit != null) {
            searchSourceBuilder.from(offset).size(limit);
        }
        if (ObjectUtil.isNotEmpty(sort)) {
            for (SortBuilder<?> sortBuilder : sort) {
                searchSourceBuilder.sort(sortBuilder);
            }
        }
        try {
            SearchResponse searchResponse = restHighLevelClient.search(
                    searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            if (searchHits == null) {
                return Pair.of(Collections.emptyList(), 0L);
            }

            List<Map<String, Object>> hits = Arrays.stream(searchHits.getHits())
                    .map(SearchHit::getSourceAsMap)
                    .collect(Collectors.toList());
            long total = searchHits.getTotalHits().value;
            return Pair.of(hits, total);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public long count(String index, QueryBuilder query) {
        CountRequest request = new CountRequest(index).query(query);
        try {
            CountResponse countResponse = restHighLevelClient.count(
                    request, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

}
