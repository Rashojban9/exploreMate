package com.exploreMate.auth_service.jwt.jwtUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private final String SECRET_KEY = "my_super_secret_key_that_is_very_long_1234567890";
    private SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private long expirationTime = 86_400_000;

    public String generateToken(String email, Set<String> role) {
        return Jwts.builder().setSubject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(key).claim("roles", role).compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Set<String> extractRoles(String token) {
        Object roleObject = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role");
        if (roleObject instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toSet());

        }
        return Set.of();
    }
}
