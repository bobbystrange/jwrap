package org.dreamcat.jwrap.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2020/3/5
 */
@Getter
@RequiredArgsConstructor
public class JwtFactory {

    public static final String TOKEN_TYPE = "Bearer";
    public static final String TOKEN_PREFIX = TOKEN_TYPE + " ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_COOKIE_NAME = "Bearer";
    public static final String TOKEN_ATTRIBUTE = "org.dreamcat.common.web.jwt";

    protected final JwtProperties jwtProperties;

    public String generateToken(String subject, String... permissions) {
        if (ObjectUtil.isEmpty(permissions)) return generateToken(subject, null, null);
        return generateToken(subject, Arrays.asList(permissions));
    }

    public String generateToken(String subject, List<String> permissions) {
        if (ObjectUtil.isEmpty(permissions)) permissions = null;
        return generateToken(subject, permissions, null);
    }

    public String generateToken(String subject, List<String> permissions,
            Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(subject)
                .setPermissions(permissions)
                .setClaims(claims)
                .issuedAt(now)
                .maxAge(jwtProperties.getMaxAge())
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public String regenerateToken(String token) {
        Jwt jwt = parse(token);
        if (jwt == null) {
            throw new IllegalArgumentException("Invalid JWT token: " + token);
        }

        long now = System.currentTimeMillis();
        jwt.setIssuedAt(now);
        jwt.setExpiredAt(now + jwtProperties.getMaxAge());
        return jwt.encode(jwtProperties.getSecretKey());
    }

    public Jwt parse(String token) {
        return Jwt.decode(token, jwtProperties.getSecretKey());
    }

    public JwtBuilder newJwt() {
        return new JwtBuilder(this.jwtProperties);
    }

    public static class JwtBuilder {

        private final JwtProperties jwtProperties;
        private final Jwt jwt;
        private Long maxAge;

        private JwtBuilder(JwtProperties jwtProperties) {
            long now = System.currentTimeMillis();
            this.jwt = new Jwt()
                    .issuedAt(now)
                    .expiredAt(Long.MAX_VALUE); // never expired
            this.jwtProperties = jwtProperties;
        }

        public JwtBuilder subject(String subject) {
            jwt.subject(subject);
            return this;
        }

        public JwtBuilder maxAge(long maxAgeInSecond) {
            this.maxAge = maxAgeInSecond;
            return this;
        }

        public String signAndCompact() {
            // since I already set expiredAt
            if (maxAge != null) {
                jwt.expiredAt(jwt.getIssuedAt() + maxAge * 1000);
            }
            return jwt.encode(jwtProperties.getSecretKey());
        }

        public JwtBuilder issuedAt(long issuedAt) {
            jwt.issuedAt(issuedAt);
            return this;
        }

        public JwtBuilder issuedAt(Date issuedAt) {
            jwt.issuedAt(issuedAt);
            return this;
        }

        public JwtBuilder expiredAt(long expiredAt) {
            jwt.expiredAt(expiredAt);
            return this;
        }

        public JwtBuilder expiredAt(Date expiredAt) {
            jwt.expiredAt(expiredAt);
            return this;
        }

        public JwtBuilder permission(String permission) {
            jwt.permission(permission);
            return this;
        }

        public JwtBuilder addPermissions(Collection<String> permissions) {
            jwt.addPermissions(permissions);
            return this;
        }

        public JwtBuilder setPermissions(Collection<String> permissions) {
            jwt.setPermissions(permissions);
            return this;
        }

        public JwtBuilder claim(String name, Object value) {
            jwt.claim(name, value);
            return this;
        }

        public JwtBuilder addClaims(Map<String, Object> claims) {
            jwt.addClaims(claims);
            return this;
        }

        public JwtBuilder setClaims(Map<String, Object> claims) {
            jwt.setClaims(claims);
            return this;
        }
    }
}
