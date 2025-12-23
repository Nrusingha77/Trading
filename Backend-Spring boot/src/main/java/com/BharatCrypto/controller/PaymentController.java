package com.BharatCrypto.controller;

import com.razorpay.RazorpayException;
import com.BharatCrypto.domain.PaymentMethod;
import com.BharatCrypto.exception.UserException;
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

    // Accept method as path variable and handle case-insensitively
    @PostMapping("/api/payment/{method}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable String method,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws UserException, RazorpayException {

        User user = userService.findUserProfileByJwt(jwt);

        // Decide payment method ignoring case
        if ("razorpay".equalsIgnoreCase(method)) {
            // create order saved in DB
            PaymentOrder order = paymentService.createOrder(user, amount, PaymentMethod.RAZORPAY);

            // generate razorpay payment link (callback url contains order id)
            PaymentResponse paymentResponse = paymentService.createRazorpayPaymentLink(user, amount, order.getId());

            return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
        }

        // If you add other methods later (stripe etc), handle them here:
        if ("stripe".equalsIgnoreCase(method)) {
            // TODO: implement stripe flow or return meaningful error for now
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }

        // unsupported payment method
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
