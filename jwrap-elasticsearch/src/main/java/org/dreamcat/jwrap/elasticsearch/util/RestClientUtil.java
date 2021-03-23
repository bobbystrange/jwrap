package org.dreamcat.jwrap.elasticsearch.util;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.dreamcat.common.util.ObjectUtil;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * Create by tuke on 2021/3/23
 */
public final class RestClientUtil {

    private RestClientUtil(){
    }

    public static RestClientBuilder restClientBuilder(String host) {
        return restClientBuilder(host, ElasticsearchConstants.REST_PORT);
    }

    public static RestClientBuilder restClientBuilder(String host, int port) {
        return restClientBuilder(host, port, null, null);
    }

    public static RestClientBuilder restClientBuilder(String host, int port, String username, String password) {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(host, port));
        if (ObjectUtil.isNotBlank(username) && ObjectUtil.isNotBlank(password)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            restClientBuilder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        return restClientBuilder;
    }
}
