package com.BharatCrypto.service;

import com.razorpay.RazorpayException;
import com.BharatCrypto.domain.PaymentMethod;
import com.BharatCrypto.model.PaymentOrder;
import com.BharatCrypto.model.User;
import com.BharatCrypto.response.PaymentResponse;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean ProccedPaymentOrder (PaymentOrder paymentOrder,
                                 String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLink(User user,
                                              Long Amount,
                                              Long orderId) throws RazorpayException;
}
