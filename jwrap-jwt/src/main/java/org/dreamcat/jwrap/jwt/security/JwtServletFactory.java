package org.dreamcat.jwrap.jwt.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.web.util.ServletUtil;
import org.dreamcat.jwrap.jwt.Jwt;
import org.dreamcat.jwrap.jwt.JwtFactory;
import org.dreamcat.jwrap.jwt.JwtProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Create by tuke on 2020/3/5
 */
@Slf4j
@Getter
public final class JwtServletFactory extends JwtFactory {

    public JwtServletFactory(JwtProperties jwtProperties) {
        super(jwtProperties);
    }

    public String getToken(HttpServletRequest request) {
        if (jwtProperties.isStoreInCookie()) {
            return getTokenFromCookie(request);
        } else {
            return getTokenFromHeader(request);
        }
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        if (request == null) return null;
        Cookie[] cookies = request.getCookies();
        if (ObjectUtil.isEmpty(cookies)) return null;
        for (Cookie cookie : cookies) {
            if (!TOKEN_COOKIE_NAME.equals(cookie.getName())) continue;
            return cookie.getValue();
        }
        return null;
    }

    public String getTokenFromHeader(HttpServletRequest request) {
        if (request == null) return null;
        String token = request.getHeader(TOKEN_HEADER);
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public void putToken(HttpServletResponse response, String token) {
        if (jwtProperties.isStoreInCookie()) {
            putTokenToCookie(response, token);
        } else {
            putTokenToHeader(response, token);
        }
    }

    public void putTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) Math.max(jwtProperties.getMaxAge(), Integer.MAX_VALUE));
        response.addCookie(cookie);
    }

    public void putTokenToHeader(HttpServletResponse response, String token) {
        response.setHeader(TOKEN_HEADER, TOKEN_PREFIX + token);
    }

    public String getSubject(HttpServletRequest request) {
        Jwt jwt = getJwt(request);
        if (jwt == null) return null;
        return jwt.getSubject();
    }

    public Set<String> getPermissions(HttpServletRequest request) {
        Jwt jwt = getJwt(request);
        if (jwt == null) return null;
        return jwt.getPermissions();
    }

    public Map<String, Object> getClaims(HttpServletRequest request) {
        Jwt jwt = getJwt(request);
        if (jwt == null) return null;
        return jwt.getClaims();
    }

    public Jwt getJwt() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return getJwt((ServletRequestAttributes) attributes);
    }

    public Jwt getJwt(ServletRequestAttributes attributes) {
        Object jwt = attributes.getAttribute(
                TOKEN_ATTRIBUTE,
                RequestAttributes.SCOPE_REQUEST);
        if (jwt == null) return null;
        return (Jwt) jwt;
    }

    public Jwt getJwt(HttpServletRequest request) {
        Object jwt = request.getAttribute(TOKEN_ATTRIBUTE);
        if (jwt == null) return null;
        return (Jwt) jwt;
    }

    public void putJwt(HttpServletRequest request, Jwt jwt) {
        request.setAttribute(TOKEN_ATTRIBUTE, jwt);
    }

    public void generateAndSetToken(String subject, String... permissions) {
        if (ObjectUtil.isEmpty(permissions)) {
            generateAndSetToken(subject, null, null);
        } else {
            generateAndSetToken(subject, Arrays.asList(permissions));
        }
    }

    public void generateAndSetToken(String subject, List<String> permissions) {
        if (ObjectUtil.isEmpty(permissions)) permissions = null;
        generateAndSetToken(subject, permissions, null);
    }

    public void generateAndSetToken(String subject, List<String> permissions,
            Map<String, Object> claims) {
        String token = generateToken(subject, permissions, claims);
        HttpServletResponse response = ServletUtil.getResponse();
        if (response == null) {
            throw new IllegalStateException(
                    "There is not a RequestAttributes currently bounded to the thread");
        }
        putToken(response, token);
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
