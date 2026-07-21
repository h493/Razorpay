package com.capstone.razorpay.vault.service.impl;

import com.capstone.razorpay.common.entity.Money;
import com.capstone.razorpay.common.enums.CardBrand;
import com.capstone.razorpay.common.exception.ResourceNotFoundException;
import com.capstone.razorpay.common.util.RandomizerUtil;
import com.capstone.razorpay.payment.processor.PaymentProcessorRouter;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.capstone.razorpay.vault.config.VaultEncryptionConfig;
import com.capstone.razorpay.vault.dto.request.TokenizeRequest;
import com.capstone.razorpay.vault.dto.response.TokenizeResponse;
import com.capstone.razorpay.vault.entity.CardToken;
import com.capstone.razorpay.vault.entity.VaultCard;
import com.capstone.razorpay.vault.repository.CardTokenRepository;
import com.capstone.razorpay.vault.repository.VaultCardRepository;
import com.capstone.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor dekEncrypter;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {
        String lastFour = request.pan().substring(request.pan().length()-4);
        String bin = request.pan().substring(0, 6);
        CardBrand cardBrand = detectBrand(request.pan());

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncrypter(dek)
                .encrypt(request.pan().getBytes(StandardCharsets.UTF_8));

        byte[] encryptedDek = dekEncrypter.encrypt(dek);

        VaultCard vaultCard = VaultCard.builder()
                .brand(cardBrand)
                .expiryYear(request.expiryYear().toString())
                .expiryMonth(request.expiryMonth().toString())
                .lastFour(lastFour)
                .bin(bin)
                .encryptedDek(encryptedDek)
                .encryptedPan(encryptedPan)
                .build();
        vaultCard = vaultCardRepository.save(vaultCard);

        String token = "tok_" + RandomizerUtil.randomBase64(32);

        cardTokenRepository.save(CardToken.builder()
                        .vaultCard(vaultCard)
                        .token(token)
                        .customer(request.customerId())
                        .merchant(merchantId)
                .build());

        return new TokenizeResponse(token, lastFour, cardBrand, request.expiryMonth(), request.expiryYear());
    }

    @Override
    public PaymentProcessorResponse charge(String token, UUID paymentId, Money amount, Map<String, Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("CardToken", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes = null;
        try {
            byte[] dek = dekEncrypter.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncrypter(dek).decrypt(vaultCard.getEncryptedDek());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest
                    .card(paymentId, pan, expiry, amount, methodDetails);

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);
            log.info("Vault Charge Register , token = {}****", token.substring(0, 4));

            return paymentProcessorResponse;
        }catch (Exception e){
            log.warn("Vault charge failed, token={}****", token.substring(0,4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        }finally {
            if(panBytes != null) Arrays.fill(panBytes, (byte) 0);
        }
    }

    private CardBrand detectBrand(String pan) {
        if(pan.startsWith("4")) return CardBrand.VISA;
        if(pan.startsWith("2") || pan.startsWith("5")) return CardBrand.MASTERCARD;
        if(pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;

        return CardBrand.RUPAY;
    }
}
