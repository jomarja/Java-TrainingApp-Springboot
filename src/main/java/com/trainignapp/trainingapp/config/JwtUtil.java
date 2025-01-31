package com.trainignapp.trainingapp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey SECRET_KEY;
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT Secret must be at least 32 characters long!");
        }
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(SECRET_KEY) // ✅ Secure key
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY) // Secure key
                .build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}