package com.BharatCrypto.service;

import com.BharatCrypto.model.PaymentDetails;
import com.BharatCrypto.model.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifsc, String bankName, User user);
    public PaymentDetails getUsersPaymentDetails(User user);
}