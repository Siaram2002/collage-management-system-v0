package com.cms.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // MUST be minimum 32 characters (256-bit)
    private static final String SECRET = "THIS_IS_A_LONG_SECURE_SECRET_KEY_1234567890";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // ------------------------------------------------
    // 1️⃣ Generate JWT Token
    // ------------------------------------------------
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ------------------------------------------------
    // 2️⃣ Extract Username
    // ------------------------------------------------
    public String extractUsername(String token) {
        return parseToken(token).getBody().getSubject();
    }

    // ------------------------------------------------
    // 3️⃣ Validate Token Against UserDetails
    // ------------------------------------------------
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------------------------
    // 4️⃣ Check Expiry
    // ------------------------------------------------
    public boolean isTokenExpired(String token) {
        return parseToken(token).getBody().getExpiration().before(new Date());
    }

    // ------------------------------------------------
    // 5️⃣ Parse Token (Single Method)
    // ------------------------------------------------
    private Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }
}
