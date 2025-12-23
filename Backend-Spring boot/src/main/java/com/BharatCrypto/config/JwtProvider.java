package com.BharatCrypto.config;

import com.BharatCrypto.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtProvider {

    private static SecretKey signingKey;
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24; // 24 hours

    public static SecretKey getSigningKey() {
    
        if (JwtConstant.SECRET_BASE64 == null || JwtConstant.SECRET_BASE64.isEmpty()) {
            throw new IllegalStateException("JWT secret not initialized. Check application.properties has jwt.secret set.");
        }

       
        if (signingKey == null) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(JwtConstant.SECRET_BASE64);
                signingKey = Keys.hmacShaKeyFor(keyBytes);
                System.out.println("✅ JWT signing key initialized (" + keyBytes.length + " bytes)");
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("jwt.secret is not valid base64: " + e.getMessage(), e);
            }
        }
        return signingKey;
    }

   
    public static String generateToken(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User or email cannot be null");
        }
        String email = user.getEmail();
        String authorities = user.getRole().toString();
        return generateToken(email, authorities);
    }

    
    public static String generateToken(Authentication auth) {
        if (auth == null) {
            throw new IllegalArgumentException("Authentication cannot be null");
        }
        String email = auth.getName();
        String authorities = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(","));
        return generateToken(email, authorities);
    }

    
    public static String generateToken(String email, String authorities) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_MS);

        String token = Jwts.builder()
                .setSubject(email)
                .claim("email", email)
                .claim("authorities", authorities == null ? "" : authorities)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                .compact();

        System.out.println("JWT token generated for: " + email);
        return token;
    }

    public static String getEmailFromJwtToken(String jwt) {
        if (jwt == null || jwt.isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }

        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return String.valueOf(claims.get("email"));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse JWT token: " + e.getMessage(), e);
        }
    }
}
