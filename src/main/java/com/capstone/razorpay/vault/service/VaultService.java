package com.capstone.razorpay.vault.service;

import com.capstone.razorpay.common.entity.Money;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.capstone.razorpay.vault.dto.request.TokenizeRequest;
import com.capstone.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(String token, UUID paymentId, Money amount, Map<String, Object> methodDetails);
}
