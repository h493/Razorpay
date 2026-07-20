package com.capstone.razorpay.payment.controller;

import com.capstone.razorpay.payment.dto.request.PaymentInitRequest;
import com.capstone.razorpay.payment.dto.response.PaymentResponse;
import com.capstone.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    UUID merchanId = UUID.fromString("sa"); // TODO

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@Valid  @RequestBody PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchanId, request));
    }
}
