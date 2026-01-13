package com.BharatCrypto.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class AppConfig {
	
	 @Bean
	    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	                .authorizeHttpRequests(Authorize -> Authorize
	                		.requestMatchers("/auth/signup").permitAll()
	                                .requestMatchers("/auth/login").permitAll()
	                                .requestMatchers("/auth/signin").permitAll()
	                                .requestMatchers("/auth/users/**").permitAll()
	                                .requestMatchers("/auth/forgot-password").permitAll()
	                                .requestMatchers("/auth/reset-password").permitAll()
	                                .requestMatchers("/auth/verify-otp").permitAll()
	                                .requestMatchers("/oauth2/**").permitAll()
	                                .requestMatchers("/static/**").permitAll()
	                                
	                                .requestMatchers("/api/**").authenticated()
	                                .requestMatchers("/admin/**").hasRole("ADMIN")

	                                .anyRequest().authenticated()
	                )
	                .csrf(csrf -> csrf.disable())
	                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class);
	               
			
			return http.build();
			
		}
		
	    // CORS Configuration
	    private CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowedOrigins(Arrays.asList(
	            "http://localhost:5173",
	            "http://localhost:3000",
	            "http://localhost:8080"
	        ));
	        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
	        configuration.setAllowedHeaders(Arrays.asList("*"));
	        configuration.setAllowCredentials(true);
	        configuration.setMaxAge(3600L);

	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }

	    @Bean
	    PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
	    @Bean
	    public ObjectMapper objectMapper() {
	        ObjectMapper mapper = new ObjectMapper();
	        // ignore extra fields from CoinCap (e.g. "rank")
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        // register java time module if needed
	        mapper.registerModule(new JavaTimeModule());
	        return mapper;
	    }

	    @Bean
	    public RestTemplate restTemplate(RestTemplateBuilder builder) {
	        return builder
	                .setConnectTimeout(Duration.ofMillis(5000)) // 5-second connection timeout
	                .setReadTimeout(Duration.ofMillis(50000))   // 30-second read timeout
	                .build();
	    }

}
