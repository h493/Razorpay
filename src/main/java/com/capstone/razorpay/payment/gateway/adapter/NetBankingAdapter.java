package com.capstone.razorpay.payment.gateway.adapter;

import com.capstone.razorpay.payment.gateway.PaymentAdapter;
import com.capstone.razorpay.payment.gateway.dto.PaymentRequest;
import com.capstone.razorpay.payment.gateway.dto.PaymentResult;

public class NetBankingAdapter implements PaymentAdapter {

    @Override
    public PaymentResult initiate(PaymentRequest request){
            return null;
    }
}
