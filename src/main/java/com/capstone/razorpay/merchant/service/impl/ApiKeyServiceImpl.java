package com.capstone.razorpay.merchant.service.impl;

import com.capstone.razorpay.common.exception.ResourceNotFoundException;
import com.capstone.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.capstone.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.capstone.razorpay.merchant.entity.ApiKey;
import com.capstone.razorpay.merchant.entity.Merchant;
import com.capstone.razorpay.merchant.repository.ApiKeyRepository;
import com.capstone.razorpay.merchant.repository.MerchantRepository;
import com.capstone.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {
    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "rzp_" + request.environment().name().toUpperCase() + "big_random_string";
        String rawSecret = "big_random_secret"; // TODO: replace with cryptography

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(rawSecret) //TODO : encode with BcryprtPasswordEncoder
                .environment(request.environment())
                .build();


        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(), rawSecret, apiKey.getEnvironment().name());
    }
}
