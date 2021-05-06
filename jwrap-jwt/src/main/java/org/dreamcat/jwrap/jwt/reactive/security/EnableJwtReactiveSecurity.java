package org.dreamcat.jwrap.jwt.reactive.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Create by tuke on 2020/3/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({JwtReactiveSecurityAutoConfiguration.class})
public @interface EnableJwtReactiveSecurity {

}
