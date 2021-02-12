package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;

/**
 * Create by tuke on 2021/1/15
 */
@Slf4j
public class EsDocumentComponent {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    public boolean insert(String index, String id, String json) {
        IndexRequest request = new IndexRequest(index)
                .id(id)
                .source(json, XContentType.JSON)
                .opType(DocWriteRequest.OpType.CREATE)
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        try {
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("insert into index {}, result: {}", index, response);
            }
            return response.getResult() == DocWriteResponse.Result.CREATED;
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public boolean update(String index, String id, String json) {
        return save(index, id, json, false);
    }

    public boolean save(String index, String id, String json) {
        return save(index, id, json, true);
    }

    private boolean save(String index, String id, String json, boolean upsert) {
        UpdateRequest request = new UpdateRequest(index, id)
                .doc(json, XContentType.JSON)
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        if (upsert) request.docAsUpsert(true);

        try {
            UpdateResponse response = restHighLevelClient.update(
                    request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("save(upsert={}) for index {}, result: {}",
                        upsert, index, response);
            }
            if (upsert) {
                return response.getResult() == DocWriteResponse.Result.UPDATED ||
                        response.getResult() == DocWriteResponse.Result.CREATED;
            } else {
                return response.getResult() == DocWriteResponse.Result.UPDATED;
            }
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public boolean delete(String index, String id) {
        DeleteRequest request = new DeleteRequest(index, id)
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            DeleteResponse response = restHighLevelClient.delete(
                    request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("delete from index {}, result: {}", index, response);
            }
            return response.getResult() == DocWriteResponse.Result.DELETED;
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public int updateByQuery(String index, Script script, QueryBuilder query) {
        UpdateByQueryRequest request = new UpdateByQueryRequest(index)
                .setScript(script)
                .setQuery(query)
                .setRefresh(true);
        try {
            BulkByScrollResponse response = restHighLevelClient.updateByQuery(
                    request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("updateByQuery for index {}, result: {}", index, response);
            }
            return response.getBatches();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public int deleteByQuery(String index, QueryBuilder query) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index)
                .setQuery(query)
                .setRefresh(true);
        try {
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(
                    request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("deleteByQuery from index {}, result: {}", index, response);
            }
            return response.getBatches();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public boolean bulk(List<DocWriteRequest<?>> bulkRequests) {
        BulkRequest request = new BulkRequest()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        for (DocWriteRequest<?> bulkRequest : bulkRequests) {
            request.add(bulkRequest);
        }
        try {
            BulkResponse response = restHighLevelClient.bulk(
                    request, RequestOptions.DEFAULT);
            if (log.isDebugEnabled()) {
                log.debug("bulk by {}, result: {}", bulkRequests, response);
            }
            boolean hasFailures = response.hasFailures();
            if (hasFailures) {
                log.error(response.buildFailureMessage());
            }
            return !hasFailures;
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }
}
