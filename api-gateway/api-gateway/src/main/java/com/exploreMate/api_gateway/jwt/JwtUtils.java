package com.exploreMate.api_gateway.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Configuration
public class JwtUtils {
    private String SECRET_KEY="my_super_secret_key_that_is_very_long_1234567890";
    private SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    public String extractUsername(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
            return true;
        }catch (Exception ex){
            return false;

        }
    }
    public Set<String> extractRoles(String token){
        Object roleObject=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role");
        if(roleObject instanceof List<?> list){
            return list.stream().map(String::valueOf).collect(Collectors.toSet());
        }
        return Set.of();
    }
}
