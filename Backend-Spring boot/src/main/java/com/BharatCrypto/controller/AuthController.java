package com.BharatCrypto.controller;

import com.BharatCrypto.config.JwtProvider;
import com.BharatCrypto.exception.UserException;
import com.BharatCrypto.model.User;
import com.BharatCrypto.repository.UserRepository;
import com.BharatCrypto.request.SignUpRequest;
import com.BharatCrypto.response.AuthResponse;
import com.BharatCrypto.service.AuthService;
import com.BharatCrypto.service.EmailService;
import com.BharatCrypto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody SignUpRequest req) throws UserException {
        try {
            System.out.println("📝 Signup request received: " + req.getEmail());
            

            User savedUser = userService.registerUser(req);
            System.out.println("User registered: " + savedUser.getEmail());


            Authentication auth = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword(), savedUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);


            String jwt = JwtProvider.generateToken(auth);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setStatus(true);
            authResponse.setMessage("User registered successfully");

            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Signup error: " + e.getMessage());
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setStatus(false);
            errorResponse.setMessage("Signup failed: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody SignUpRequest req) throws UserException {
        System.out.println("Login request for: " + req.getEmail());

        User user = userRepository.findByEmail(req.getEmail());
        if (user == null) {
            throw new UserException("User not found");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new UserException("Invalid credentials");
        }

        String jwt = JwtProvider.generateToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Login successful");

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody SignUpRequest req) throws UserException {
        return login(req); // Delegate to login
    }

    @GetMapping("/validate")
    public ResponseEntity<User> validateToken(@RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
