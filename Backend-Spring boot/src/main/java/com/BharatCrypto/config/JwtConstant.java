package com.BharatCrypto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConstant {
    public static final String JWT_HEADER = "Authorization";
    
    public static String SECRET_BASE64;

    @Value("${jwt.secret}")
    public void setSecretBase64(String secret) {
        if (secret == null || secret.isEmpty() || secret.contains("REPLACE")) {
            throw new IllegalArgumentException(" jwt.secret is not configured in application.properties");
        }
        JwtConstant.SECRET_BASE64 = secret;
        System.out.println("✅ JWT secret loaded: " + secret.substring(0, 20) + "...");
    }
}
