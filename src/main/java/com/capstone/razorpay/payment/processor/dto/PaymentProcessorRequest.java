package com.capstone.razorpay.payment.processor.dto;

import com.capstone.razorpay.common.entity.Money;
import com.capstone.razorpay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String,Object> methodDetails
) {


}
