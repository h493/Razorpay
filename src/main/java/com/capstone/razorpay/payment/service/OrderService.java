package com.capstone.razorpay.payment.service;

import com.capstone.razorpay.payment.dto.request.CreateOrderRequest;
import com.capstone.razorpay.payment.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
}
