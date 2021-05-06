package org.dreamcat.jwrap.jwt.reactive.security;

import org.dreamcat.common.web.security.AnonymityProperties;
import org.dreamcat.jwrap.jwt.JwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create by tuke on 2020/3/19
 */
@Configuration
@EnableConfigurationProperties({AnonymityProperties.class, JwtProperties.class})
public class JwtReactiveSecurityAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public JwtReactiveFactory jwtReactiveFactory(
            JwtProperties jwtProperties) {
        return new JwtReactiveFactory(jwtProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public JwtReactiveFilter jwtReactiveFilter(
            JwtReactiveFactory jwtReactiveFactory,
            AnonymityProperties anonymityProperties) {
        return new JwtReactiveFilter(jwtReactiveFactory, anonymityProperties);
    }
}
