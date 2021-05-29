package org.dreamcat.jwrap.jwt;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Create by tuke on 2020/4/11
 */
public final class Jwts {

    private Jwts() {
    }

    public static Jwt decode(String token, String key) {
        return Jwt.decode(token, key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Jwt jwt;
        private String token;
        private Long ttlMs;

        private Builder() {
            long now = System.currentTimeMillis();
            this.jwt = new Jwt()
                    .issuedAt(now)
                    // never expired
                    .expiredAt(Long.MAX_VALUE);
        }

        public Builder subject(String subject) {
            jwt.subject(subject);
            return this;
        }

        public Builder ttlMs(long ttlMs) {
            this.ttlMs = ttlMs;
            return this;
        }

        public Builder signWith(Jwt.Algorithm algorithm, String key) {
            if (algorithm != null) {
                jwt.algorithm(algorithm);
            }
            // since I already set expiredAt
            if (ttlMs != null) {
                jwt.expiredAt(jwt.getIssuedAt() + ttlMs);
            }
            this.token = jwt.encode(key);
            return this;
        }

        public Builder signWith(String key) {
            return signWith(null, key);
        }

        public String compact() {
            if (token == null) {
                throw new UnsupportedOperationException(
                        "expect to invoke 'signWith' before invoking 'compact'");
            }
            return token;
        }

        public Builder issuedAt(long issuedAt) {
            jwt.issuedAt(issuedAt);
            return this;
        }

        public Builder issuedAt(Date issuedAt) {
            jwt.issuedAt(issuedAt);
            return this;
        }

        public Builder expiredAt(long expiredAt) {
            jwt.expiredAt(expiredAt);
            return this;
        }

        public Builder expiredAt(Date expiredAt) {
            jwt.expiredAt(expiredAt);
            return this;
        }

        public Builder permission(String permission) {
            jwt.permission(permission);
            return this;
        }

        public Builder addPermissions(Collection<String> permissions) {
            jwt.addPermissions(permissions);
            return this;
        }

        public Builder setPermissions(Collection<String> permissions) {
            jwt.setPermissions(permissions);
            return this;
        }

        public Builder claim(String name, Object value) {
            jwt.claim(name, value);
            return this;
        }

        public Builder addClaims(Map<String, Object> claims) {
            jwt.addClaims(claims);
            return this;
        }

        public Builder setClaims(Map<String, Object> claims) {
            jwt.setClaims(claims);
            return this;
        }
    }
}
