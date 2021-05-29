package org.dreamcat.jwrap.jwt;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Create by tuke on 2020/2/26
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProperties {

    private String secretKey;
    private long ttlMs;
    // add extra CORS headers
    private boolean enableCors;
    // store token in cookie rather than header
    private boolean storeInCookie;
}
