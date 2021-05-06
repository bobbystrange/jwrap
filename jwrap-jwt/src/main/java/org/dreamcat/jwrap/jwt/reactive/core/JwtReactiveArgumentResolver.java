package org.dreamcat.jwrap.jwt.reactive.core;

import java.util.Optional;
import org.dreamcat.jwrap.jwt.Jwt;
import org.dreamcat.jwrap.jwt.JwtFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Create by tuke on 2020/6/4
 */
@Component
public class JwtReactiveArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Jwt.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext,
            ServerWebExchange exchange) {
        return Mono.justOrEmpty(
                Optional.ofNullable(exchange.getAttribute(JwtFactory.TOKEN_ATTRIBUTE)));
    }

}
