package org.dreamcat.jwrap.mybatis.config;

import java.util.Objects;
import java.util.function.IntSupplier;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.web.context.ClassPathEnumScanner;
import org.dreamcat.jwrap.mybatis.type.IntSupplierTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

/**
 * Create by tuke on 2021/5/6
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(IntSupplierTypeProperties.class)
public class IntSupplierTypeConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public ConfigurationCustomizer configreCustomizer(IntSupplierTypeProperties intSupplierTypeProperties) {

        return configurationCustomizer -> {
            String[] enumBasePackages = intSupplierTypeProperties.getEnumBasePackages();
            if (ObjectUtil.isEmpty(enumBasePackages)) return;

            TypeHandlerRegistry typeHandlerRegistry = configurationCustomizer.getTypeHandlerRegistry();
            ClassPathEnumScanner classPathEnumScanner = new ClassPathEnumScanner();
            for (String enumBasePackage : enumBasePackages) {
                for (BeanDefinition beanDefinition : classPathEnumScanner
                        .findCandidateComponents(enumBasePackage)) {

                    Class<? extends IntSupplier> type = (Class<? extends IntSupplier>) ClassUtils
                            .resolveClassName(Objects.requireNonNull(beanDefinition.getBeanClassName()),
                                    ClassUtils.getDefaultClassLoader());
                    if (!type.isAnonymousClass() && type.isEnum()) {
                        typeHandlerRegistry.register(type, IntSupplierTypeHandler.class);
                    }
                }
            }
        };
    }
}
