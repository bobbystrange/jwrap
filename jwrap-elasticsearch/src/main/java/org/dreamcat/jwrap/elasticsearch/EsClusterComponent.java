package org.dreamcat.jwrap.elasticsearch;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Create by tuke on 2021/3/20
 */
@Slf4j
@RequiredArgsConstructor
public class EsClusterComponent {

    private final RestHighLevelClient restHighLevelClient;

    /**
     * see following link for more information
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html
     *
     * @param autoCreateIndex my-index-000001,index10,-index1*,+ind*
     *                        false
     *                        true
     * @return success or not
     */
    public boolean updatePersistentActionAutoCreateIndex(String autoCreateIndex) {
        return updatePersistentSettings(Collections.singletonMap(
                AUTO_CREATE_INDEX, autoCreateIndex));
    }

    public boolean updatePersistentSettings(Map<String, ?> source) {
        ClusterUpdateSettingsRequest request = new ClusterUpdateSettingsRequest();
        request.persistentSettings(source);
        try {
            ClusterUpdateSettingsResponse response = restHighLevelClient.cluster()
                    .putSettings(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    private static final String AUTO_CREATE_INDEX = "action.auto_create_index";
}
