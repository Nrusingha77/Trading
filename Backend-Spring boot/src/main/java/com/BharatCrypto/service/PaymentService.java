package com.BharatCrypto.service;

import com.BharatCrypto.domain.PaymentMethod;
import com.BharatCrypto.model.PaymentOrder;
import com.BharatCrypto.model.User;
import com.BharatCrypto.response.PaymentResponse;
import com.razorpay.Payment;
import com.razorpay.RazorpayException;

public interface PaymentService {
    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);
    PaymentOrder getPaymentOrderById(Long id) throws Exception;
    Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException;
    PaymentResponse createRazorpayPaymentLink(User user, Long amount, Long orderId) throws RazorpayException;
    Payment getRazorpayPayment(String paymentId) throws RazorpayException;
}