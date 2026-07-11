package com.capstone.razorpay.common.enums;

public enum PaymentStatus {
    CREATED,
    AUTHORIZING,
    AUTHORIZED,
    CAPTURING,
    CAPTURED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    SETTLED,
    CANCELLED,
    FAILED,
    AUTH_EXPIRED
}
