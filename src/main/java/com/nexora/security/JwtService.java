package com.nexora.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode(secret)
        );

        this.expiration = expiration;
    }

    // Generate JWT token
    public String generateToken(String email) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // Extract all claims from token
    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract email from token
    public String extractEmail(String token) {

        return extractClaims(token).getSubject();
    }

    // Validate token
    public boolean isTokenValid(String token, String email) {

        Claims claims = extractClaims(token);

        return email.equals(claims.getSubject())
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }
}