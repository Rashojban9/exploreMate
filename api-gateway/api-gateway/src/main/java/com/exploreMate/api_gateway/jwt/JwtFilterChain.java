package com.exploreMate.api_gateway.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilterChain extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.contains("/public/") || path.contains("/auth-service/") || path.contains("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }
            if (!jwtUtils.validateToken(token)) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String username = jwtUtils.extractUsername(token);
            String email = jwtUtils.extractEmail(token);
            Set<String> roles = jwtUtils.extractRoles(token);
            var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            // Add X-User-Email header for trip and saved item routes
            if (path.contains("/api/trips") || path.contains("/api/saved")) {
                request = new HttpServletRequestWrapper(request, email);
            }
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    // Inner class to wrap request with custom header
    private static class HttpServletRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final String email;
        
        public HttpServletRequestWrapper(HttpServletRequest request, String email) {
            super(request);
            this.email = email;
        }
        
        @Override
        public String getHeader(String name) {
            if ("X-User-Email".equals(name)) {
                return email;
            }
            return super.getHeader(name);
        }
    }
}
