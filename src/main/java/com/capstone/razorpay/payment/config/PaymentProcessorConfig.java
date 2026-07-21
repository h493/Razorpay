package com.capstone.razorpay.payment.config;

import com.capstone.razorpay.common.enums.PaymentMethod;
import com.capstone.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.capstone.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.capstone.razorpay.payment.processor.PaymentProcessor;
import com.capstone.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.capstone.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.capstone.razorpay.payment.processor.strategy.UPIPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final UPIPaymentProcessor upiPaymentProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD, cardPaymentProcessor,
                PaymentMethod.NET_BANKING, netBankingPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor
        );
    }
}
