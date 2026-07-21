package com.capstone.razorpay.payment.config;

import com.capstone.razorpay.common.enums.PaymentMethod;
import com.capstone.razorpay.payment.gateway.PaymentAdapter;
import com.capstone.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.capstone.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.capstone.razorpay.payment.gateway.adapter.UPIPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UPIPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NET_BANKING, netBankingAdapter,
                PaymentMethod.UPI, upiPaymentAdapter
        );
    }
}
