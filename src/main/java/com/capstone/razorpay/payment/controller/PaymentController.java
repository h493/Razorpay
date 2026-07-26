package com.capstone.razorpay.payment.controller;

import com.capstone.razorpay.payment.dto.request.PaymentInitRequest;
import com.capstone.razorpay.payment.dto.response.PaymentResponse;
import com.capstone.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    UUID merchantId = UUID.fromString("becf6766-f7a6-4cb0-8db8-45f9d2b633aa"); // TODO: replace with authenticated merchant id

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@Valid  @RequestBody PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantId, request));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId){
        return ResponseEntity.ok(paymentService.capture(merchantId, paymentId));
    }
}
