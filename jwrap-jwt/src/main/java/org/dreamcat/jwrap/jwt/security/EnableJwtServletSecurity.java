package org.dreamcat.jwrap.jwt.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Create by tuke on 2020/2/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({JwtServletSecurityAutoConfiguration.class})
public @interface EnableJwtServletSecurity {

}

