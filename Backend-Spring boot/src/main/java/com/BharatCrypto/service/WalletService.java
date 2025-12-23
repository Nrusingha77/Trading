package com.BharatCrypto.service;


import com.BharatCrypto.exception.WalletException;
import com.BharatCrypto.model.Order;
import com.BharatCrypto.model.User;
import com.BharatCrypto.model.Wallet;

public interface WalletService {


    Wallet getUserWallet(User user) throws WalletException;

    public Wallet addBalanceToWallet(Wallet wallet, Long money) throws WalletException;

    public Wallet findWalletById(Long id) throws WalletException;

    public Wallet walletToWalletTransfer(User sender,Wallet receiverWallet, Long amount) throws WalletException;

    public Wallet payOrderPayment(Order order, User user) throws WalletException;



}
