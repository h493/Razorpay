package com.capstone.razorpay.merchant.service;

import com.capstone.razorpay.merchant.dto.request.LoginRequest;
import com.capstone.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.capstone.razorpay.merchant.dto.response.LoginResponse;
import com.capstone.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(LoginRequest request);
}
