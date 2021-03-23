package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.core.Pair;
import org.dreamcat.common.x.jackson.JacksonUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.ToXContent.MapParams;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;

/**
 * Create by tuke on 2021/1/14
 * <p/>
 * There is a limit per shard (2billion)
 * but this is not the limit for an index as an index can have multiple shards.
 */
@Slf4j
@RequiredArgsConstructor
public class EsIndexComponent {

    private final RestHighLevelClient restHighLevelClient;

    public List<String> getAllIndex() {
        GetIndexRequest request = new GetIndexRequest("_all");
        GetIndexResponse response;
        try {
            response = restHighLevelClient.indices()
                    .get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
        return Arrays.asList(response.getIndices());
    }

    public boolean createIndex(String index) {
        return createIndex(index, (String) null, null);
    }

    public boolean createIndex(
            String index, @Nullable Map<String, Object> mapping, @Nullable String settings) {
        return createIndex(index, JacksonUtil.toJson(mapping), settings);
    }

    public boolean createIndex(
            String index, @Nullable String mapping, @Nullable String settings) {
        CreateIndexRequest request = new CreateIndexRequest(index);
        if (mapping != null) {
            request.mapping(mapping, XContentType.JSON);
        }
        if (settings != null) {
            request.settings(settings, XContentType.JSON);
        }
        try {
            CreateIndexResponse response = restHighLevelClient.indices()
                    .create(request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("create index {}, result: {}", request.index(), response);
            }
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public boolean deleteIndex(String index) {
        if (!existsIndex(index)) {
            return true;
        }
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = restHighLevelClient.indices()
                    .delete(request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("delete index {}, result: {}", index, response);
            }
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public boolean existsIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return restHighLevelClient.indices()
                    .exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    /**
     * @param index index name
     * @return mapping and settings
     */
    public Pair<String, String> getIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            GetIndexResponse response = restHighLevelClient.indices()
                    .get(request, RequestOptions.DEFAULT);
            MappingMetadata mapping = response.getMappings().get(index);
            Settings settings = response.getSettings().get(index);
            return Pair.of(JacksonUtil.toJson(mapping.getSourceAsMap()), parseSettings(settings));
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    private String parseSettings(Settings settings) {
        try (XContentBuilder builder = XContentBuilder.builder(XContentType.JSON.xContent())) {
            builder.startObject();
            settings.toXContent(builder,
                    new MapParams(Collections.singletonMap("flat_settings", "false")));
            builder.endObject();
            return Strings.toString(builder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void reindex(String sourceIndex, String destIndex) {
        reindex(sourceIndex, destIndex, null);
    }

    public void reindex(
            String sourceIndex, String destIndex, QueryBuilder queryBuilder) {
        ReindexRequest request = new ReindexRequest()
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
        ReindexRequest request = new ReindexRequest()
                .setSourceIndices(sourceIndex)
                .setDestIndex(destIndex);
        if (queryBuilder != null) {
            request.setSourceQuery(queryBuilder);
        }
        restHighLevelClient.reindexAsync(request, RequestOptions.DEFAULT, listener);
    }

}
