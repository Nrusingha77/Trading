package com.BharatCrypto.service;

import com.BharatCrypto.domain.VerificationType;
import com.BharatCrypto.model.ForgotPasswordToken;
import com.BharatCrypto.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp,
                                    VerificationType verificationType,String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);

    boolean verifyToken(ForgotPasswordToken token,String otp);
}
