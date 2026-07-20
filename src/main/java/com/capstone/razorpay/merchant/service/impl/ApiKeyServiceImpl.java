package com.capstone.razorpay.merchant.service.impl;

import com.capstone.razorpay.common.exception.ResourceNotFoundException;
import com.capstone.razorpay.common.util.RandomizerUtil;
import com.capstone.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.capstone.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.capstone.razorpay.merchant.dto.response.ApiKeyResponse;
import com.capstone.razorpay.merchant.entity.ApiKey;
import com.capstone.razorpay.merchant.entity.Merchant;
import com.capstone.razorpay.merchant.mapper.ApiKeyMapper;
import com.capstone.razorpay.merchant.repository.ApiKeyRepository;
import com.capstone.razorpay.merchant.repository.MerchantRepository;
import com.capstone.razorpay.merchant.service.ApiKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {
    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "rzp_" + request.environment().name().toLowerCase() + "_" + RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(rawSecret) //TODO : encode with BcryprtPasswordEncoder
                .environment(request.environment())
                .build();


        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(), rawSecret, apiKey.getEnvironment().name());
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
        return apiKeyMapper.toResponseList(apiKeyRepository.findByMerchant_Id(merchantId));
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("api-key", keyId));


        apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey); // even if we skip this it will happen because of persistence context we got in transactional
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("api-key", keyId));

        if(!apiKey.isEnabled()){
            throw new RuntimeException("Cannot rotate disable key");
        }

        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(newRawSecret);  //TODO : encode with BcryprtPasswordEncoder
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiryAt(LocalDateTime.now().plusHours(24));

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(),
                newRawSecret, apiKey.getEnvironment().name());
    }
}
