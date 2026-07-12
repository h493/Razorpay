package com.capstone.razorpay.merchant.service.impl;

import com.capstone.razorpay.common.enums.MerchantStatus;
import com.capstone.razorpay.common.enums.UserRole;
import com.capstone.razorpay.common.exception.DuplicateResourceException;
import com.capstone.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.capstone.razorpay.merchant.dto.response.MerchantResponse;
import com.capstone.razorpay.merchant.entity.AppUser;
import com.capstone.razorpay.merchant.entity.Merchant;
import com.capstone.razorpay.merchant.repository.AppUserRepository;
import com.capstone.razorpay.merchant.repository.MerchantRepository;
import com.capstone.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT", "Merchant with email already exists : " + request.email());
        }

        Merchant mercant = Merchant.builder()
                .name(request.name())
                .email(request.email())
                .businessName(request.businessName())
                .businessType(request.businessType())
                .status(MerchantStatus.PENDING_KYC)
                .build();

        mercant = merchantRepository.save(mercant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .merchant(mercant)
                .passwordHash(request.password()) // TODO : encrypt using Bcrypt
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);

        return new MerchantResponse(mercant.getId(), mercant.getName(),
                mercant.getEmail(), mercant.getBusinessName(),
                mercant.getBusinessType(), mercant.getStatus());
    }
}
