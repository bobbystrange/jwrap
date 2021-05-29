package org.dreamcat.jwrap.jwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Create by tuke on 2020/3/5
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JwtServletSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtServletFilter jwtServletFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // no csrf
                .csrf().disable()
                // deny xframe
                .headers().frameOptions().disable()
                // enable cors
                //.and().cors()
                .and()
                // no cookie
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // .and()
                // .exceptionHandling().authenticationEntryPoint(
                // (request, response, authException) ->
                //         response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                //                 "Unauthorized in error handler"))
                .and()
                .authorizeRequests().anyRequest().permitAll()
                // no need login
                //.antMatchers(HttpMethod.OPTIONS, "/**").anonymous()
                .and()
                .addFilterBefore(jwtServletFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // JwtServletFilter will ignore the below paths
        web.ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                );
    }
}
