package com.capstone.razorpay.payment.service.impl;

import com.capstone.razorpay.common.enums.OrderStatus;
import com.capstone.razorpay.common.enums.PaymentEvent;
import com.capstone.razorpay.common.enums.PaymentStatus;
import com.capstone.razorpay.common.exception.BusinessRuleViolationException;
import com.capstone.razorpay.common.exception.ResourceNotFoundException;
import com.capstone.razorpay.payment.dto.request.PaymentInitRequest;
import com.capstone.razorpay.payment.dto.response.PaymentResponse;
import com.capstone.razorpay.payment.entity.OrderRecord;
import com.capstone.razorpay.payment.entity.Payment;
import com.capstone.razorpay.payment.gateway.PaymentGatewayRouter;
import com.capstone.razorpay.payment.gateway.dto.PaymentRequest;
import com.capstone.razorpay.payment.gateway.dto.PaymentResult;
import com.capstone.razorpay.payment.mapper.PaymentMapper;
import com.capstone.razorpay.payment.repository.OrderRepository;
import com.capstone.razorpay.payment.repository.PaymentRepository;
import com.capstone.razorpay.payment.service.PaymentService;
import com.capstone.razorpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {
        OrderRecord order = orderRepository.findByIdAndMerchantIdForUpdate(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER", request.orderId()));

        if(order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.ATTEMPTED){
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE",
                    "Order cannot accept payment in status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .method(request.method())
                .idempotencyKey(UUID.randomUUID().toString())
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);
        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(), request.orderId(),
                merchantId, order.getAmount(), request.method(), request.methodDetails());

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result){
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationReference());
            case PaymentResult.Failure failure -> {
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAILURE);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {
                log.warn("Invalid state");
                return null;
            }
        }


        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        //TODO : Send an outbox
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {

        Payment payment = paymentRepository.findByIdAndMerchantIdForUpdate(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_ATTEMPT);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

        if(paymentResult instanceof PaymentResult.Success success){
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment captured, paymentId: {}", paymentId);
        }else if(paymentResult instanceof PaymentResult.Failure failure){
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAILURE);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
            log.warn("Payment capture failed, paymentId: {}", paymentId);
        }

        payment = paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {

        Payment payment = paymentRepository.findbyIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if(payment.getStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment is not in Authorizing state, paymentId: {}, status: {}", paymentId, payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();

        if(approve){
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBandReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            //Auto-capture
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_ATTEMPT);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

            if(captureResult instanceof PaymentResult.Success success){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if(captureResult instanceof PaymentResult.Failure failure){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAILURE);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }

        }else{
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAILURE);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);
    }
}
