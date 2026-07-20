package com.capstone.razorpay.merchant.mapper;

import com.capstone.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.capstone.razorpay.merchant.dto.response.ApiKeyResponse;
import com.capstone.razorpay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    ApiKeyCreateResponse toCreateResponse(ApiKey apiKey);

    List<ApiKeyResponse> toResponseList(List<ApiKey> apiKeys);
}
