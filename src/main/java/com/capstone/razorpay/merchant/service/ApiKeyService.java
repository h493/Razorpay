package com.capstone.razorpay.merchant.service;

import com.capstone.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.capstone.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId,CreateApiKeyRequest request);
}
