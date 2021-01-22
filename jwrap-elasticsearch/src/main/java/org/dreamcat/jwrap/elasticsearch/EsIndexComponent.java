package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/1/14
 * <p/>
 * There is a limit per shard (2billion)
 * but this is not the limit for an index as an index can have multiple shards.
 */
@Slf4j
@Component
public class EsIndexComponent {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    public boolean createIndex(String index) {
        return createIndex(index, (String) null, null);
    }

    public boolean createIndex(
            String index,
            @Nullable Object mapping,
            @Nullable Object settings) {
        return createIndex(index,
                mapping instanceof String ? (String) mapping : JacksonUtil.toJson(mapping),
                settings instanceof String ? (String) settings : JacksonUtil.toJson(settings));
    }

    public boolean createIndex(
            String index, @Nullable String mapping, @Nullable String settings) {
        var request = new CreateIndexRequest(index);
        if (mapping != null) {
            request.mapping(mapping, XContentType.JSON);
        }
        if (settings != null) {
            request.settings(settings, XContentType.JSON);
        }
        try {
            var response = restHighLevelClient.indices()
                    .create(request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("create index {}, result: {}", request.index(), response);
            }
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public boolean deleteIndex(String index) {
        if (!existsIndex(index)) {
            return true;
        }
        var request = new DeleteIndexRequest(index);
        try {
            var response = restHighLevelClient.indices()
                    .delete(request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("delete index {}, result: {}", index, response);
            }
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public boolean existsIndex(String index) {
        var request = new GetIndexRequest(index);
        try {
            return restHighLevelClient.indices()
                    .exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public GetIndexResponse getIndex(String index) {
        var request = new GetIndexRequest(index);
        try {
            return restHighLevelClient.indices()
                    .get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public void reindex(String sourceIndex, String destIndex) {
        reindex(sourceIndex, destIndex, null);
    }

    public void reindex(
            String sourceIndex, String destIndex, QueryBuilder queryBuilder) {
        var request = new ReindexRequest()
                .setSourceIndices(sourceIndex)
                .setDestIndex(destIndex);
        if (queryBuilder != null) {
            request.setSourceQuery(queryBuilder);
        }
        try {
            restHighLevelClient.reindex(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public void reindexAsync(
            String sourceIndex, String destIndex,
            ActionListener<BulkByScrollResponse> listener) {
        reindexAsync(sourceIndex, destIndex, null, listener);
    }

    public void reindexAsync(
            String sourceIndex, String destIndex, QueryBuilder queryBuilder,
            ActionListener<BulkByScrollResponse> listener) {
        var request = new ReindexRequest()
                .setSourceIndices(sourceIndex)
                .setDestIndex(destIndex);
        if (queryBuilder != null) {
            request.setSourceQuery(queryBuilder);
        }
        restHighLevelClient.reindexAsync(request, RequestOptions.DEFAULT, listener);
    }

}
