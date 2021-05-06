package org.dreamcat.jwrap.mybatis.config;

import lombok.Data;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Create by tuke on 2021/5/7
 */
@Data
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class IntSupplierTypeProperties {

    private String[] enumBasePackages;
}
