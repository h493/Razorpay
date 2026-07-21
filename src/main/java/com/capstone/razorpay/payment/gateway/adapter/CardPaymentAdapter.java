package com.capstone.razorpay.payment.gateway.adapter;

import com.capstone.razorpay.payment.gateway.PaymentAdapter;
import com.capstone.razorpay.payment.gateway.dto.PaymentRequest;
import com.capstone.razorpay.payment.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CardPaymentAdapter implements PaymentAdapter {

    @Override
    public PaymentResult initiate(PaymentRequest request){
        return null;
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
