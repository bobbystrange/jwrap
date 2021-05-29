package org.dreamcat.jwrap.jwt.security;

import org.dreamcat.common.web.security.AnonymityProperties;
import org.dreamcat.jwrap.jwt.JwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Create by tuke on 2020/3/5
 */
@Configuration
@EnableConfigurationProperties({AnonymityProperties.class, JwtProperties.class})
@Import({JwtServletSecurityConfig.class})
public class JwtServletSecurityAutoConfiguration {

    //@Bean
    //@ConditionalOnMissingBean
    //public CorsConfigurationSource corsConfigurationSource() {
    //    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //    CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
    //    corsConfiguration.addExposedHeader(JwtServletFactory.TOKEN_RESPONSE_HEADER);
    //    corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
    //    corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
    //    corsConfiguration.setAllowCredentials(false);
    //    corsConfiguration.setMaxAge(3600L);
    //    source.registerCorsConfiguration("/**", corsConfiguration);
    //    return source;
    //}

    @Bean
    @ConditionalOnMissingBean
    public JwtServletFactory jwtServletFactory(JwtProperties jwtProperties) {
        return new JwtServletFactory(jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtServletFilter jwtServletFilter(
            JwtServletFactory jwtServletFactory,
            AnonymityProperties anonymityProperties) {
        return new JwtServletFilter(jwtServletFactory, anonymityProperties);
    }

}
