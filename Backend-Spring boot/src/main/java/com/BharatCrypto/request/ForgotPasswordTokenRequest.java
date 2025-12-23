package com.BharatCrypto.request;

import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {
    private String email;
    private String otp;
}