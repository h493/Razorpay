package com.capstone.razorpay.payment.gateway;

import com.capstone.razorpay.common.enums.PaymentMethod;
import com.capstone.razorpay.payment.gateway.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapterMap;

    public void initiate(PaymentRequest paymentRequest){
        PaymentAdapter paymentAdapter = paymentAdapterMap.get(paymentRequest.method());
        if(paymentAdapter == null){
            throw new IllegalArgumentException("No Payment Adapter Register for method: " + paymentRequest.method());
        }
        paymentAdapter.initiate(paymentRequest);
    }
}
