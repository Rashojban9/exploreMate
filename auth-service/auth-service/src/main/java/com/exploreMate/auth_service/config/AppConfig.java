package com.exploreMate.auth_service.config;

import com.exploreMate.auth_service.jwt.jwtUtils.JwtFilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor

public class AppConfig {
    private final JwtFilterChain jwtFilterChain;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/public/**", "/api/public/**", "/error", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**", "/auth-service/webjars/**").permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilterChain, UsernamePasswordAuthenticationFilter.class)
                .build();

    }


}
