package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey key;
    private final Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
        logger.info("JwtUtil initialized with expiration: {} ms", expiration);
    }

    public String generateToken(Long userId) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration);

            String token = Jwts.builder()
                    .setSubject(String.valueOf(userId))
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key)
                    .compact();
            
            logger.debug("Generated token for user {}: {}", userId, token);
            return token;
        } catch (Exception e) {
            logger.error("Error generating token for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error generating token", e);
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());
            logger.debug("Extracted user ID {} from token", userId);
            return userId;
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage(), e);
            throw new RuntimeException("Error extracting user ID from token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            
            logger.debug("Token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage(), e);
            return false;
        }
    }
} 