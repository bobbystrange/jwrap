package org.dreamcat.jwrap.jwt.reactive.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.jwrap.jwt.Jwt;
import org.dreamcat.jwrap.jwt.JwtFactory;
import org.dreamcat.jwrap.jwt.JwtProperties;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;

/**
 * Create by tuke on 2020/3/19
 */
public class JwtReactiveFactory extends JwtFactory {

    public JwtReactiveFactory(JwtProperties jwtProperties) {
        super(jwtProperties);
    }

    public String getToken(ServerHttpRequest request) {
        if (jwtProperties.isStoreInCookie()) {
            return getTokenFromCookie(request);
        } else {
            return getTokenFromHeader(request);
        }
    }

    public String getTokenFromCookie(ServerHttpRequest request) {
        if (request == null) return null;
        List<HttpCookie> cookies = request.getCookies().get(TOKEN_COOKIE_NAME);
        if (ObjectUtil.isEmpty(cookies)) return null;
        return cookies.get(0).getName();
    }

    public String getTokenFromHeader(ServerHttpRequest request) {
        if (request == null) return null;
        String token = request.getHeaders().getFirst(TOKEN_HEADER);
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public void putToken(ServerHttpResponse response, String token) {
        if (jwtProperties.isStoreInCookie()) {
            putTokenToCookie(response, token);
        } else {
            putTokenToHeader(response, token);
        }
    }

    public void putTokenToCookie(ServerHttpResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .maxAge(jwtProperties.getMaxAge())
                .build();
        response.addCookie(cookie);
    }

    public void putTokenToHeader(ServerHttpResponse response, String token) {
        response.getHeaders().set(TOKEN_HEADER, TOKEN_PREFIX + token);
    }

    public String getSubject(ServerWebExchange exchange) {
        Jwt jwt = getJwt(exchange);
        if (jwt == null) return null;
        return jwt.getSubject();
    }

    public Set<String> getPermissions(ServerWebExchange exchange) {
        Jwt jwt = getJwt(exchange);
        if (jwt == null) return null;
        return jwt.getPermissions();
    }

    public Map<String, Object> getClaims(ServerWebExchange exchange) {
        Jwt jwt = getJwt(exchange);
        if (jwt == null) return null;
        return jwt.getClaims();
    }

    public Jwt getJwt(ServerWebExchange exchange) {
        Object jwt = exchange.getAttributes().get(TOKEN_ATTRIBUTE);
        if (jwt == null) return null;
        return (Jwt) jwt;
    }

    public void setJwt(ServerWebExchange exchange, Jwt jwt) {
        exchange.getAttributes().put(TOKEN_ATTRIBUTE, jwt);
    }

    /**
     * get authentication information
     *
     * @param jwt parse(token)
     * @return authentication information
     * @see #parse(String)
     */
    public UsernamePasswordAuthenticationToken getAuthentication(Jwt jwt) {
        String subject = jwt.getSubject();
        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
        Set<String> permissions = jwt.getPermissions();
        if (ObjectUtil.isNotEmpty(permissions)) {
            authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return new UsernamePasswordAuthenticationToken(subject, null, authorities);
    }
}
