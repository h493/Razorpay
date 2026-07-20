package com.capstone.razorpay.merchant.mapper;

import com.capstone.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.capstone.razorpay.merchant.dto.response.MerchantResponse;
import com.capstone.razorpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    Merchant toEntityFromSignUpRequest(MerchantSignupRequest request);

    MerchantResponse toResponse(Merchant merchant);
}
