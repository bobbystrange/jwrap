package org.dreamcat.jwrap.elasticsearch;

import javax.annotation.Resource;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Create by tuke on 2021/1/15
 * -Dspring.elasticsearch.rest.uris=http://192.168.1.255:9200
 */
@SpringBootApplication
public class DataApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Bean
    public EsIndexComponent esIndexComponent() {
        return new EsIndexComponent(restHighLevelClient);
    }

    @Bean
    public EsDocumentComponent esDocumentComponent() {
        return new EsDocumentComponent(restHighLevelClient);
    }

    @Bean
    public EsSearchComponent esSearchComponent() {
        return new EsSearchComponent(restHighLevelClient);
    }
}
