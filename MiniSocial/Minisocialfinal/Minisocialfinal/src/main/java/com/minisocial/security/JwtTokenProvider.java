package com.minisocial.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;

@ApplicationScoped
public class JwtTokenProvider {
    
    private static final Logger LOGGER = Logger.getLogger(JwtTokenProvider.class.getName());
    
    private static final String SECRET_KEY = "c2VjcmV0LWtleS1mb3ItbWluaXNvY2lhbC1hcHBsaWNhdGlvbi1zZWN1cmUta2V5LWZvci1qd3QtdG9rZW5z";
    private static final long VALIDITY_IN_MILLISECONDS = 3600000; // 1 hour
    
    private Key key;
    
    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize JWT token provider: " + e.getMessage());
            throw new RuntimeException("Failed to initialize JWT token provider", e);
        }
    }
    
    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_IN_MILLISECONDS);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }
    
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.warning("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }
} 