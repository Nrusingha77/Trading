package com.BharatCrypto.service;

import com.BharatCrypto.domain.WalletTransactionType;
import com.BharatCrypto.model.Wallet;
import com.BharatCrypto.model.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {
    WalletTransaction createTransaction(Wallet wallet, WalletTransactionType type, String transferId, String purpose, Long amount);
    List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type);
}