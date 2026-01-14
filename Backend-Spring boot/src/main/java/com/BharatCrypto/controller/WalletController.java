package com.BharatCrypto.controller;

import com.BharatCrypto.domain.WalletTransactionType;
import com.BharatCrypto.model.*;
import com.BharatCrypto.response.DepositResponse;
import com.BharatCrypto.service.*;
import com.razorpay.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import java.util.List;

@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    @GetMapping("/api/wallet")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction req
    ) throws Exception {
        User senderUser = userService.findUserProfileByJwt(jwt);
        Wallet receiverWallet = walletService.findWalletById(walletId);
        Wallet wallet = walletService.walletToWalletTransfer(senderUser, receiverWallet, req.getAmount());

        walletTransactionService.createTransaction(wallet, WalletTransactionType.WALLET_TRANSFER, receiverWallet.getId().toString(), req.getPurpose(), -req.getAmount());

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/deposit")
    public ResponseEntity<DepositResponse> addMoneyToWallet(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(name="order_id") Long orderId,
            @RequestParam(name="payment_id") String paymentId
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        PaymentOrder order = paymentService.getPaymentOrderById(orderId);
        Boolean status = paymentService.ProceedPaymentOrder(order, paymentId);
        Payment payment = paymentService.getRazorpayPayment(paymentId);

        if(status){
            wallet = walletService.addBalance(wallet, order.getAmount());
            walletTransactionService.createTransaction(wallet, WalletTransactionType.ADD_MONEY, null, "Add Money via Razorpay", order.getAmount());
        }

        DepositResponse res = new DepositResponse();
        res.setWallet(wallet);
        res.setMessage("Deposite success");
        res.setAmount(String.valueOf(order.getAmount()));
        
        JSONObject json = payment.toJson();
        String method = json.has("method") ? json.getString("method") : "";
        res.setPaymentMethod(method);
        
        String details = "";
        if(json.has("bank") && !json.isNull("bank")) {
            details = json.getString("bank");
        } else if(json.has("wallet") && !json.isNull("wallet")) {
            details = json.getString("wallet");
        } else if(json.has("vpa") && !json.isNull("vpa")) {
            details = json.getString("vpa");
        } else if(json.has("card_id") && !json.isNull("card_id")) {
            details = "****"; // Masked for security, or fetch card details if needed
        }
        res.setPaymentDetails(details);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/api/wallet/transactions")
    public ResponseEntity<List<WalletTransaction>> getWalletTransactions(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        List<WalletTransaction> transactions = walletTransactionService.getTransactions(wallet, null);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}