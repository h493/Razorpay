package com.capstone.razorpay.payment.api;

import com.capstone.razorpay.payment.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentLookupService {
    List<Payment> findUnsettlementCapturedPayments(UUID merchantId);
}
