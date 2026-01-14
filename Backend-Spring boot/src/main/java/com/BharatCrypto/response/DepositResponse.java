package com.BharatCrypto.response;

import com.BharatCrypto.model.Wallet;
import lombok.Data;

@Data
public class DepositResponse {
    private Wallet wallet;
    private String message;
    private String amount;
    private String paymentMethod;
    private String paymentDetails;
}