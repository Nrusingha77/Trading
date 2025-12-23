package com.BharatCrypto.service;

import com.BharatCrypto.model.User;

public interface AuthService {
    String generateToken(User user);
}