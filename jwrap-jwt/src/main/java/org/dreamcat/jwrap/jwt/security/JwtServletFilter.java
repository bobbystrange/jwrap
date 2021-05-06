package org.dreamcat.jwrap.jwt.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dreamcat.common.web.security.AnonymityProperties;
import org.dreamcat.common.web.util.ServletUtil;
import org.dreamcat.jwrap.jwt.Jwt;
import org.dreamcat.jwrap.jwt.JwtFactory;
import org.dreamcat.jwrap.jwt.JwtProperties;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Create by tuke on 2020/3/19
 */
public class JwtServletFilter extends OncePerRequestFilter {

    private final JwtServletFactory jwtFactory;
    private final JwtProperties jwtProperties;
    private final AnonymityProperties anonymityProperties;

    public JwtServletFilter(JwtServletFactory jwtFactory, AnonymityProperties anonymityProperties) {
        this.jwtFactory = jwtFactory;
        this.jwtProperties = jwtFactory.getJwtProperties();
        this.anonymityProperties = anonymityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (configureCorsAndReturnIsPreFlight(request, response)) return;

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        if (anonymityProperties.isAnonymous(request.getRequestURI())) {
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtFactory.getToken(request);
        Jwt jwt = jwtFactory.parse(token);

        if (jwt == null || jwt.isExpired()) {
            ServletUtil.sendUnauthorized(response);
            return;
        }

        jwtFactory.putJwt(request, jwt);
        securityContext.setAuthentication(jwtFactory.getAuthentication(jwt));
        SecurityContextHolder.setContext(securityContext);
        filterChain.doFilter(request, response);
    }

    /**
     * set header for CORS
     *
     * @return true if current request is a pre-flight
     */
    protected boolean configureCorsAndReturnIsPreFlight(HttpServletRequest request,
            HttpServletResponse response) {
        if (!jwtProperties.isEnableCors()) {
            return request.getMethod().equalsIgnoreCase("options");
        }

        String origin = request.getHeader("Origin");
        if (origin == null) return false;

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "false");
        response.addHeader("Access-Control-Expose-Headers", JwtFactory.TOKEN_HEADER);
        if (!request.getMethod().equalsIgnoreCase("options")) return false;

        String allowMethod = request.getHeader("Access-Control-Request-Method");
        String allowHeaders = request.getHeader("Access-Control-Request-Headers");
        response.setHeader("Access-Control-Max-Age", "3600");
        if (allowHeaders != null) {
            response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        }
        if (allowMethod != null) {
            response.setHeader("Access-Control-Allow-Methods", allowMethod);
        }
        return true;
    }

}
