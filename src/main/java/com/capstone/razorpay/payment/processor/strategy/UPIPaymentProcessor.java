package com.capstone.razorpay.payment.processor.strategy;

import com.capstone.razorpay.payment.processor.PaymentProcessor;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class UPIPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
