package com.momo.savanger.conf.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momo.savanger.api.authtoken.AuthTokenService;
import com.momo.savanger.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserService userService;

    private final AuthTokenService authTokenService;

    @Value("${header.authtoken.name}")
    private final String authTokenHeaderName;

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain configureSecurity(HttpSecurity http,
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .userDetailsService(this.userService)
                .httpBasic(basic -> {
                    basic.authenticationEntryPoint(
                            new CustomBasicAuthEntryPoint(this.objectMapper));
                })
                .sessionManagement(sess -> {
                    sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        final AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        final TokenBasicAuthenticationFilter basicAuthenticationFilter = new TokenBasicAuthenticationFilter(
                authenticationManager,
                this.authTokenService,
                this.authTokenHeaderName
        );

        final TokenAuthenticationFilter tokenFilter = new TokenAuthenticationFilter(
                this.authTokenService, this.authTokenHeaderName
        );

        http.addFilter(basicAuthenticationFilter);
        http.addFilterBefore(tokenFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}
