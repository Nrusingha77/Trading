package com.BharatCrypto.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (shouldSkipJwtValidation(path)) {
            System.out.println("Skipping JWT validation for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank() || !header.startsWith("Bearer ")) {
            System.out.println(" No Bearer token found for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = header.substring(7).trim();
        
        if (jwt.isEmpty() || "undefined".equalsIgnoreCase(jwt) || "null".equalsIgnoreCase(jwt)) {
            System.out.println("Bearer token is invalid/empty for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            System.out.println(" Validating JWT token for path: " + path);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtProvider.getSigningKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String email = String.valueOf(claims.get("email"));
            String authorities = (String) claims.get("authorities");
            var auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities == null ? "" : authorities);

            var authentication = new UsernamePasswordAuthenticationToken(email, null, auths);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("JWT validated for user: " + email);

        } catch (JwtException e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            throw new BadCredentialsException("Invalid token: " + e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipJwtValidation(String path) {
        if (path == null) return false;

        return path.startsWith("/auth/signup")
                || path.startsWith("/auth/login")
                || path.startsWith("/auth/signin")          // ✅ ADD THIS
                || path.startsWith("/auth/forgot-password")
                || path.startsWith("/auth/reset-password")
                || path.startsWith("/auth/verify-otp")
                || path.startsWith("/oauth2/")
                || path.startsWith("/static/")
                || path.equals("/");                         // Allow root path
    }
}
