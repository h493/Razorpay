package com.capstone.razorpay.payment.processor.dto;

public interface PaymentProcessorResponse {

    record Pending(String processorReference){}

    record Success(String processorReference, String bankReference){}

    record Failure(String errorCode, String errorDescription) {}
}
