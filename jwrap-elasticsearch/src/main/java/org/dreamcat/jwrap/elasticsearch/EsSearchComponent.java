package org.dreamcat.jwrap.elasticsearch;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.core.Triple;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.dreamcat.jwrap.elasticsearch.core.EsSearchParam;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

/**
 * Create by tuke on 2021/1/20
 */
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
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
        return get(index, id, Map.class);
    }

    public <T> T get(String index, String id, Class<T> clazz) {
        GetRequest request = new GetRequest(index, id);
        try {
            GetResponse response = restHighLevelClient.get(
                    request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("get from index {}, result: {}", index, response);
            }
            return parseGetResponse(response, clazz);
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
            Map<String, Object> item = parseGetResponse(getResponse, HashMap.class);
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
            T item = parseGetResponse(getResponse, clazz);
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

    private static <T> T parseGetResponse(GetResponse response, Class<T> clazz) {
        if (!response.isExists()) {
            return null;
        }
        String json = response.getSourceAsString();
        return JacksonUtil.fromJson(json, clazz);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public long count(String index) {
        return count(index, new MatchAllQueryBuilder());
    }

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

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public List<Map<String, Object>> searchAll(
            String index, int size, TimeValue keepAlive) {
        try (ScrollMapIter scrollMapIter = scrollMapIter(index, size, keepAlive)) {
            List<Map<String, Object>> result = null;
            while (scrollMapIter.hasNext()) {
                List<Map<String, Object>> list = scrollMapIter.next();
                if (result == null) {
                    result = new ArrayList<>(scrollMapIter.total.intValue());
                }
                result.addAll(list);
            }
            return result;
        }
    }

    public <T> List<T> searchAll(
            String index, int size, TimeValue keepAlive, Class<T> clazz) {
        try (ScrollIter<T> scrollIter = scrollIter(index, size, keepAlive, clazz)) {
            List<T> result = null;
            while (scrollIter.hasNext()) {
                List<T> list = scrollIter.next();
                if (result == null) {
                    result = new ArrayList<>(scrollIter.total.intValue());
                }
                result.addAll(list);
            }
            return result;
        }
    }

    public Triple<List<Map<String, Object>>, Long, String> search(EsSearchParam search) {
        SearchRequest request = search.searchRequest();
        try {
            SearchResponse response = restHighLevelClient.search(
                    request, RequestOptions.DEFAULT);
            return parseSearchResponse(response, Map.class);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public <T> Triple<List<T>, Long, String> search(
            EsSearchParam search, Class<T> clazz) {
        SearchRequest request = search.searchRequest();
        try {
            SearchResponse response = restHighLevelClient.search(
                    request, RequestOptions.DEFAULT);
            return parseSearchResponse(response, clazz);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public Triple<List<Map<String, Object>>, Long, String> scroll(
            String scrollId, TimeValue keepAlive) {
        SearchScrollRequest request = new SearchScrollRequest(scrollId)
                .scroll(keepAlive);
        try {
            SearchResponse response = restHighLevelClient.scroll(
                    request, RequestOptions.DEFAULT);
            return parseSearchResponse(response, Map.class);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public <T> Triple<List<T>, Long, String> scroll(
            String scrollId, TimeValue keepAlive, Class<T> clazz) {
        SearchScrollRequest request = new SearchScrollRequest(scrollId)
                .scroll(keepAlive);
        try {
            SearchResponse response = restHighLevelClient.scroll(
                    request, RequestOptions.DEFAULT);
            return parseSearchResponse(response, clazz);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public boolean clearScroll(List<String> scrollIds) {
        ClearScrollRequest request = new ClearScrollRequest();
        request.scrollIds(scrollIds);
        try {
            ClearScrollResponse response = restHighLevelClient.clearScroll(
                    request, RequestOptions.DEFAULT);
            return response.isSucceeded();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    private <T> Triple parseSearchResponse(
            SearchResponse searchResponse, Class<T> clazz) {
        SearchHits searchHits = searchResponse.getHits();
        String scrollId = searchResponse.getScrollId();
        if (searchHits == null) {
            return Triple.of(Collections.emptyList(), 0L, scrollId);
        }

        SearchHit[] hits = searchHits.getHits();
        long total = searchHits.getTotalHits().value;

        List<T> items = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            String id = hit.getId();
            if (Map.class.isAssignableFrom(clazz)) {
                Map<String, Object> sourceMap = hit.getSourceAsMap();
                Map<String, Object> idMap = Collections.singletonMap("id", id);
                if (ObjectUtil.isEmpty(sourceMap)) {
                    items.add((T) idMap);
                    continue;
                }

                Map<String, Object> item = new HashMap<>(sourceMap.size() + 1);
                item.putAll(idMap);
                item.putAll(sourceMap);
                items.add((T) item);
                continue;
            }
            String sourceJson = hit.getSourceAsString();
            items.add(JacksonUtil.fromJson(sourceJson, clazz));
        }
        return Triple.of(items, total, scrollId);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public <T> ScrollIter<T> scrollIter(
            String index, int size, TimeValue keepAlive, Class<T> clazz) {
        return new ScrollIter<>(index, size, keepAlive, clazz);
    }

    public ScrollMapIter scrollMapIter(
            String index, int size, TimeValue keepAlive) {
        return new ScrollMapIter(index, size, keepAlive);
    }

    @ToString
    @RequiredArgsConstructor
    public class ScrollIter<T> implements Iterator<List<T>>, Closeable {

        final String index;
        final int size;
        final TimeValue keepAlive;
        final Class<T> clazz;

        String scrollId;
        List<String> scrollIds;
        @Getter
        Long total;
        @Getter
        Long remaining;

        @Override
        public boolean hasNext() {
            return remaining == null || remaining <= 0;
        }

        @Override
        public List<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Triple<List<T>, Long, String> chunk;
            if (remaining != null) {
                scrollIds.add(scrollId);
                chunk = EsSearchComponent.this.scroll(scrollId, keepAlive, clazz);
            } else {
                EsSearchParam esSearchParam = EsSearchParam.builder()
                        .index(index)
                        .size(size)
                        .fetchSource(true)
                        .keepAlive(keepAlive)
                        .build();
                chunk = EsSearchComponent.this.search(esSearchParam, clazz);
                remaining = chunk.second();
                // only record it
                total = remaining;
                // need scroll
                if (remaining > size) {
                    scrollIds = new ArrayList<>((int) (remaining / size));
                }
            }

            List<T> list = chunk.first();
            scrollId = chunk.third();
            remaining -= size;
            return list;
        }

        @Override
        public void close() {
            if (ObjectUtil.isNotEmpty(scrollIds)) {
                // release the resources
                boolean cleared = EsSearchComponent.this.clearScroll(scrollIds);
                if (cleared && log.isDebugEnabled()) {
                    log.debug("success to clear scroll {}", scrollIds);
                }
            }
        }
    }

    @ToString
    @RequiredArgsConstructor
    public class ScrollMapIter implements Iterator<List<Map<String, Object>>>, Closeable {

        final String index;
        final int size;
        final TimeValue keepAlive;

        String scrollId;
        List<String> scrollIds;
        @Getter
        Long total;
        @Getter
        Long remaining;

        @Override
        public boolean hasNext() {
            return remaining == null || remaining > 0;
        }

        @Override
        public List<Map<String, Object>> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Triple<List<Map<String, Object>>, Long, String> chunk;
            if (remaining != null) {
                scrollIds.add(scrollId);
                chunk = EsSearchComponent.this.scroll(scrollId, keepAlive);
            } else {
                EsSearchParam esSearchParam = EsSearchParam.builder()
                        .index(index)
                        .size(size)
                        .fetchSource(true)
                        .keepAlive(keepAlive)
                        .build();
                chunk = EsSearchComponent.this.search(esSearchParam);
                remaining = chunk.second();
                // only record it
                total = remaining;
                // need scroll
                if (remaining > size) {
                    scrollIds = new ArrayList<>((int) (remaining / size));
                }
            }

            List<Map<String, Object>> list = chunk.first();
            scrollId = chunk.third();
            remaining -= size;
            return list;
        }

        @Override
        public void close() {
            if (ObjectUtil.isNotEmpty(scrollIds)) {
                // release the resources
                boolean cleared = EsSearchComponent.this.clearScroll(scrollIds);
                if (cleared && log.isDebugEnabled()) {
                    log.debug("success to clear scroll {}", scrollIds);
                }
            }
        }
    }
}
