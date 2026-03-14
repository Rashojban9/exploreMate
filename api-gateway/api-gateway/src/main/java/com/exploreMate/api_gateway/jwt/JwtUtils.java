package com.exploreMate.api_gateway.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${jwt.secret:my_super_secret_key_that_is_very_long_1234567890}")
    private String SECRET_KEY;
    
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String extractEmail(String token) {
        // Try to get email from claims, fallback to subject (username)
        Object emailObj = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("email");
        if (emailObj != null) {
            return emailObj.toString();
        }
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Set<String> extractRoles(String token) {
        var payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        // Look for "roles" (auth-service style) or "role" (api-gateway legacy style)
        Object roleObject = payload.get("roles");
        if (roleObject == null) {
            roleObject = payload.get("role");
        }
        
        if (roleObject instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toSet());
        } else if (roleObject instanceof String s) {
            return Set.of(s);
        }
        return Set.of();
    }
}
