package org.dreamcat.jwrap.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.crypto.SignUtil;
import org.dreamcat.common.util.Base64Util;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.x.jackson.JacksonUtil;

/**
 * Create by tuke on 2020/2/26
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Jwt {

    @Setter
    @JsonProperty("s")
    private String subject;
    @JsonSerialize(using = AlgorithmSerializer.class)
    @JsonDeserialize(using = AlgorithmDeserializer.class)
    @JsonProperty("a")
    private Algorithm algorithm;
    @JsonProperty("p")
    private Set<String> permissions;
    @JsonProperty("c")
    private Map<String, Object> claims;
    @JsonProperty("i")
    private long issuedAt;
    @JsonProperty("e")
    private long expiredAt;

    public Jwt() {
        this.algorithm = Algorithm.HS512;
    }

    /**
     * get JWT instance
     *
     * @param token token string: payload.sign
     * @param key   key to sign the payload
     * @return null if invalid token
     * @see java.util.Base64.Decoder#decode(String)
     */
    public static Jwt decode(String token, String key) {
        if (token == null) return null;
        String[] parts = token.split("\\.");
        if (parts.length != 2) return null;

        String rawPayload = parts[0];
        String sign = parts[1];

        String playload;
        try {
            playload = Base64Util.decodeAsString(rawPayload);
        } catch (IllegalArgumentException e) {
            // if playload is not in valid Base64 scheme
            return null;
        }
        // invalid json / unsupported algorithm
        Jwt jwt = JacksonUtil.fromJson(playload, Jwt.class);
        if (jwt == null || jwt.subject == null || jwt.algorithm == null) return null;

        String newSign;
        try {
            newSign = jwt.algorithm.digest(rawPayload, key);
        } catch (Exception ignored) {
            return null;
        }
        // invalid sign
        if (!sign.equals(newSign)) return null;
        return jwt;
    }

    public String encode(String key) {
        String payload = JacksonUtil.toJson(this);
        payload = Base64Util.encodeAsString(Objects.requireNonNull(payload));
        String sign = algorithm.digest(payload, key);
        return payload + "." + sign;
    }

    @JsonIgnore
    public boolean isExpired() {
        return expiredAt < System.currentTimeMillis();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public Jwt subject(String subject) {
        this.subject = subject;
        return this;
    }

    public Jwt algorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public Jwt issuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
        return this;
    }

    public Jwt issuedAt(Date issuedAt) {
        this.issuedAt = issuedAt.getTime();
        return this;
    }

    public Jwt expiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public Jwt expiredAt(Date expiredAt) {
        this.expiredAt = expiredAt.getTime();
        return this;
    }

    public Jwt permission(String permission) {
        if (permissions == null) {
            permissions = new HashSet<>();
        }
        permissions.add(permission);
        return this;
    }

    public Jwt addPermissions(Collection<String> list) {
        if (permissions == null) {
            permissions = new HashSet<>();
        }
        permissions.addAll(list);
        return this;
    }

    public Jwt setPermissions(Collection<String> list) {
        if (ObjectUtil.isEmpty(list)) {
            this.permissions = null;
        } else {
            this.permissions = new HashSet<>(list);
        }
        return this;
    }

    public Jwt claim(String name, Object value) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        claims.put(name, value);
        return this;
    }

    public Jwt addClaims(Map<String, Object> map) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        claims.putAll(map);
        return this;
    }

    public Jwt setClaims(Map<String, Object> map) {
        if (ObjectUtil.isEmpty(map)) {
            this.claims = null;
        } else {
            this.claims = new HashMap<>(map);
        }
        return this;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @RequiredArgsConstructor
    public enum Algorithm {
        HS512(SignUtil::hs512Base64),
        HS256(SignUtil::hs256Base64);

        private final BinaryOperator<String> operator;

        public String digest(String data, String key) {
            return operator.apply(data, key);
        }
    }

    private static class AlgorithmSerializer extends JsonSerializer<Algorithm> {

        @Override
        public void serialize(Algorithm value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(value.name());
        }
    }

    private static class AlgorithmDeserializer extends JsonDeserializer<Algorithm> {

        @Override
        public Algorithm deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String name = p.getValueAsString();
            try {
                return Algorithm.valueOf(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

}
