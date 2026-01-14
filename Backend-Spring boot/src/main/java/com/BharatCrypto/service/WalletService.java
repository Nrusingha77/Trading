package com.BharatCrypto.service;

import com.BharatCrypto.model.User;
import com.BharatCrypto.model.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, Long money);
    Wallet findWalletById(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) throws Exception;
    Wallet payOrderPayment(Long orderId, User user, Long amount) throws Exception;
}