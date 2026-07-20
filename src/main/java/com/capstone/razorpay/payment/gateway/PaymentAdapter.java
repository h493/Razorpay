package com.capstone.razorpay.payment.gateway;

import com.capstone.razorpay.payment.gateway.dto.PaymentRequest;

public interface PaymentAdapter {
    void initiate(PaymentRequest request);
}
