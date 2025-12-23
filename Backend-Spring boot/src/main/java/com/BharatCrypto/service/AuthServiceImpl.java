package com.BharatCrypto.service;

import com.BharatCrypto.config.JwtProvider;
import com.BharatCrypto.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String generateToken(User user) {
        // Create an Authentication object from the user
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                user.getAuthorities()
        );
        // Generate and return JWT token
        return JwtProvider.generateToken(auth);
    }
}