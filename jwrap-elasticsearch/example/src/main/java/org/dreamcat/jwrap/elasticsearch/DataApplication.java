package org.dreamcat.jwrap.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Create by tuke on 2021/1/15
 */
@SpringBootApplication
public class DataApplication {

    @Bean
    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }
}
