package com.capstone.razorpay.payment.gateway;

import com.capstone.razorpay.payment.gateway.dto.PaymentRequest;
import com.capstone.razorpay.payment.gateway.dto.PaymentResult;

public interface PaymentAdapter {
    PaymentResult initiate(PaymentRequest request);
}
