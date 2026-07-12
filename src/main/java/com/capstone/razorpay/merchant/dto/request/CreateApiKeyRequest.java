package com.capstone.razorpay.merchant.dto.request;

import com.capstone.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
