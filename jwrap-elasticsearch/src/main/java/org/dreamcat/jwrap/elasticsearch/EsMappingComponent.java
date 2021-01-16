package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2021/1/15
 * <p/>
 * if the field exists, you typically have to reindex.
 */
@Slf4j
@Component
public class EsMappingComponent {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    public boolean putMapping(String mapping) {
        var request = new PutMappingRequest()
                .source(mapping, XContentType.JSON);

        try {
            var response = restHighLevelClient.indices()
                    .putMapping(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public Cancellable putMappingAsync(
            String mapping, ActionListener<AcknowledgedResponse> listener) {
        var request = new PutMappingRequest()
                .source(mapping, XContentType.JSON);
        return restHighLevelClient.indices()
                .putMappingAsync(request, RequestOptions.DEFAULT, listener);
    }

}
