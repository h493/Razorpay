package com.capstone.razorpay.payment.config;

import com.capstone.razorpay.common.enums.PaymentMethod;
import com.capstone.razorpay.payment.processor.PaymentProcessor;
import com.capstone.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.capstone.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.capstone.razorpay.payment.processor.strategy.UPIPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NET_BANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UPIPaymentProcessor()
        );
    }
}
