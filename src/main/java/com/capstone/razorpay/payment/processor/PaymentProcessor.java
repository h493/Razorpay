package com.capstone.razorpay.payment.processor;

import com.capstone.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
