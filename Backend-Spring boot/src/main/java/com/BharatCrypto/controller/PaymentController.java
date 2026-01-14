package com.BharatCrypto.controller;

import com.BharatCrypto.domain.PaymentMethod;
import com.BharatCrypto.model.PaymentOrder;
import com.BharatCrypto.model.User;
import com.BharatCrypto.response.PaymentResponse;
import com.BharatCrypto.service.PaymentService;
import com.BharatCrypto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payment/{method}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable String method,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        if ("razorpay".equalsIgnoreCase(method)) {
            PaymentOrder order = paymentService.createOrder(user, amount, PaymentMethod.RAZORPAY);
            PaymentResponse paymentResponse = paymentService.createRazorpayPaymentLink(user, amount, order.getId());
            return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}