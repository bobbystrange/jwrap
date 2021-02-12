package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Create by tuke on 2021/1/15
 * <p/>
 * if the field exists, you typically have to reindex.
 */
@Slf4j
@RequiredArgsConstructor
public class EsMappingComponent {

    private final RestHighLevelClient restHighLevelClient;

    public boolean putMapping(String index, String mapping) {
        PutMappingRequest request = new PutMappingRequest(index)
                .source(mapping, XContentType.JSON);

        try {
            AcknowledgedResponse response = restHighLevelClient.indices()
                    .putMapping(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public Cancellable putMappingAsync(
            String index, String mapping,
            ActionListener<AcknowledgedResponse> listener) {
        PutMappingRequest request = new PutMappingRequest(index)
                .source(mapping, XContentType.JSON);
        return restHighLevelClient.indices()
                .putMappingAsync(request, RequestOptions.DEFAULT, listener);
    }

}
