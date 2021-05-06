package org.dreamcat.jwrap.jwt.reactive.security;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.web.security.AnonymityProperties;
import org.dreamcat.jwrap.jwt.Jwt;
import org.dreamcat.jwrap.jwt.JwtFactory;
import org.dreamcat.jwrap.jwt.JwtProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Create by tuke on 2020/3/19
 */
public class JwtReactiveFilter implements WebFilter {

    private final JwtReactiveFactory jwtFactory;
    private final JwtProperties jwtProperties;
    private final AnonymityProperties anonymityProperties;

    public JwtReactiveFilter(JwtReactiveFactory jwtFactory,
            AnonymityProperties anonymityProperties) {
        this.jwtFactory = jwtFactory;
        this.jwtProperties = jwtFactory.getJwtProperties();
        this.anonymityProperties = anonymityProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (configureCorsAndReturnIsPreFlight(request, response)) {
            return chain.filter(exchange);
        }

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        if (anonymityProperties.isAnonymous(request.getPath().value())) {
            SecurityContextHolder.setContext(securityContext);
            return chain.filter(exchange);
        }

        String token = jwtFactory.getToken(request);
        Jwt jwt = jwtFactory.parse(token);

        if (jwt == null || jwt.isExpired()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.empty());
        }

        jwtFactory.setJwt(exchange, jwt);
        securityContext.setAuthentication(jwtFactory.getAuthentication(jwt));
        SecurityContextHolder.setContext(securityContext);
        return chain.filter(exchange);
    }

    /**
     * set header for CORS
     *
     * @return true if current request is a pre-flight
     */
    protected boolean configureCorsAndReturnIsPreFlight(ServerHttpRequest request,
            ServerHttpResponse response) {
        if (!jwtProperties.isEnableCors()) {
            return HttpMethod.OPTIONS.equals(request.getMethod());
        }

        HttpHeaders requestHeaders = request.getHeaders();
        String origin = requestHeaders.getOrigin();
        if (origin == null) return false;

        HttpHeaders responseHeaders = response.getHeaders();
        // Access-Control-Allow-Origin
        responseHeaders.setAccessControlAllowOrigin(origin);
        responseHeaders.setAccessControlAllowCredentials(false);
        // Access-Control-Expose-Headers
        List<String> exposeHeaders = new ArrayList<>(
                responseHeaders.getAccessControlExposeHeaders());
        if (!exposeHeaders.contains(JwtFactory.TOKEN_HEADER)) {
            exposeHeaders.add(JwtFactory.TOKEN_HEADER);
        }
        responseHeaders.setAccessControlExposeHeaders(exposeHeaders);
        if (!HttpMethod.OPTIONS.equals(request.getMethod())) return false;

        // ==== ==== ==== ==== Set Headers for OPTIONS ==== ==== ==== ====

        responseHeaders.setAccessControlMaxAge(Duration.ofHours(1));

        List<HttpMethod> allowMethods = new ArrayList<>();
        allowMethods.add(HttpMethod.OPTIONS);

        HttpMethod accessControlRequestMethod = requestHeaders.getAccessControlRequestMethod();
        if (accessControlRequestMethod != null) {
            allowMethods.add(accessControlRequestMethod);
        }
        responseHeaders.setAccessControlAllowMethods(allowMethods);

        List<String> accessControlRequestHeaders = requestHeaders.getAccessControlRequestHeaders();
        if (ObjectUtil.isNotEmpty(accessControlRequestHeaders)) {
            responseHeaders.setAccessControlAllowHeaders(accessControlRequestHeaders);
        }
        return true;
    }

}
