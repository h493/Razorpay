package com.capstone.razorpay.payment.config;

import com.capstone.razorpay.common.enums.PaymentMethod;
import com.capstone.razorpay.payment.gateway.PaymentAdapter;
import com.capstone.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.capstone.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.capstone.razorpay.payment.gateway.adapter.UPIPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.NET_BANKING, new NetBankingAdapter(),
                PaymentMethod.UPI, new UPIPaymentAdapter()
        );
    }
}
