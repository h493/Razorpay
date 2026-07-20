package com.capstone.razorpay.payment.service.impl;

import com.capstone.razorpay.common.enums.OrderStatus;
import com.capstone.razorpay.common.exception.BusinessRuleViolationException;
import com.capstone.razorpay.common.exception.DuplicateResourceException;
import com.capstone.razorpay.common.exception.ResourceNotFoundException;
import com.capstone.razorpay.payment.dto.request.CreateOrderRequest;
import com.capstone.razorpay.payment.dto.response.OrderResponse;
import com.capstone.razorpay.payment.dto.response.PaymentResponse;
import com.capstone.razorpay.payment.entity.OrderRecord;
import com.capstone.razorpay.payment.entity.Payment;
import com.capstone.razorpay.payment.mapper.OrderMapper;
import com.capstone.razorpay.payment.mapper.PaymentMapper;
import com.capstone.razorpay.payment.repository.OrderRepository;
import com.capstone.razorpay.payment.repository.PaymentRepository;
import com.capstone.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order with receipt already exists: " + request.receipt());
        }

        OrderRecord order = OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .notes(request.notes())
                .merchantId(merchantId)
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(request.expiresAt() != null ? request.expiresAt() :
                        LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();

        order = orderRepository.save(order);

        // TODO : publish kafka event about order creation


        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
         //merchantId ->  any merchant should not able to see other merchant order data
         OrderRecord orderRecord =  orderRepository.findByIdAndMerchantId(orderId, merchantId)
                 .orElseThrow(() -> new ResourceNotFoundException("order", orderId));
         return orderMapper.toResponse(orderRecord);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord order =  orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("order", orderId));

        if(OrderStatus.CANCELLED == order.getOrderStatus() || OrderStatus.PAID == order.getOrderStatus()){
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCLEL",
                    "Cannot cancel order with status: " + order.getOrderStatus().name());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("order", orderId));

        List<Payment> paymentList = paymentRepository.findByOrder_Id(orderId);

        return paymentMapper.toResponseList(paymentList);
    }
}
