package org.dreamcat.jwrap.jwt.core;

import org.dreamcat.jwrap.jwt.Jwt;
import org.dreamcat.jwrap.jwt.JwtFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Create by tuke on 2020/6/4
 * <pre>
 * {@code @Bean public JwtArgumentResolver JwtArgumentResolver() {return new JwtArgumentResolver();}}
 * </pre>
 */
@Component
public class JwtArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Jwt.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return webRequest.getAttribute(JwtFactory.TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }
}
