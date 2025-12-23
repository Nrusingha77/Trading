package com.BharatCrypto.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String password;
    private String fullName;
}