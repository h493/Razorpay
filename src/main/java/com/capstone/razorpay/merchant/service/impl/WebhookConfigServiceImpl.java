package com.capstone.razorpay.merchant.service.impl;

import com.capstone.razorpay.common.util.RandomizerUtil;
import com.capstone.razorpay.merchant.api.MerchantWebhookApi;
import com.capstone.razorpay.merchant.dto.request.UpdateWebhookConfigRequest;
import com.capstone.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.capstone.razorpay.common.dto.WebhookTarget;
import com.capstone.razorpay.merchant.entity.Merchant;
import com.capstone.razorpay.merchant.entity.MerchantWebhookConfig;
import com.capstone.razorpay.merchant.mapper.WebhookConfigMapper;
import com.capstone.razorpay.merchant.repository.MerchantRepository;
import com.capstone.razorpay.merchant.repository.WebhookConfigRepository;
import com.capstone.razorpay.merchant.service.WebhookConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookConfigServiceImpl implements WebhookConfigService, MerchantWebhookApi {

    private final MerchantRepository merchantRepository;
    private final WebhookConfigRepository webhookConfigRepository;
    private final BytesEncryptor bytesEncryptor;
    private final WebhookConfigMapper webhookConfigMapper;

    @Override
    public WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found with id: " + merchantId));

        String rawSecret = RandomizerUtil.randomBase64(32);
        byte[] rawSecretBytes = rawSecret.getBytes();
        String encryptedSecret = Base64.getEncoder()
                .encodeToString(bytesEncryptor.encrypt(rawSecretBytes));


        MerchantWebhookConfig config = MerchantWebhookConfig.builder()
                .merchant(merchant)
                .targetUrl(request.targetUrl())
                .enabled(true)
                .eventTypes(request.eventTypes())
                .webhookSecret(encryptedSecret)
                .build();

        config = webhookConfigRepository.save(config);

        return webhookConfigMapper.toResponse(config, rawSecret);
    }

    @Override
    public List<WebhookConfigResponse> list(UUID merchantId) {
        return webhookConfigRepository.findByMerchant_Id(merchantId).stream()
                .map(config -> webhookConfigMapper.toResponse(config, null))
                .toList();
    }

    @Override
    public WebhookConfigResponse getById(UUID merchantId, UUID configId) {
       MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
       return webhookConfigMapper.toResponse(config, null);
    }

    @Override
    @Transactional
    public WebhookConfigResponse update(UUID merchantId, UUID configId, UpdateWebhookConfigRequest request) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        config.setTargetUrl(request.targetUrl());
        config.setEventTypes(request.eventTypes());
        log.info("Updating webhook config: {}", config.getId());
        return webhookConfigMapper.toResponse(config, null);
    }

    @Override
    @Transactional
    public void delete(UUID merchantId, UUID configId) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        webhookConfigRepository.delete(config);
        log.info("Deleted webhook config: {}", config.getId());
    }

    private MerchantWebhookConfig requireOwnedConfig(UUID merchantId, UUID configId){
        return webhookConfigRepository.findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(() -> new RuntimeException("Config not found or not owned by merchant: " + merchantId));
    }

    @Override
    public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType) {
        return webhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId).stream()
                .filter(config -> config.isSubscribedTo(eventType))
                .map(config -> {
                    byte[] cipherBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(cipherBytes);
                    return new WebhookTarget(config.getId(), config.getTargetUrl(), new String(decryptedSecretBytes, StandardCharsets.UTF_8));
                })
                .toList();

    }
}
