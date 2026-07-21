package com.capstone.razorpay.payment.processor.strategy;

import com.capstone.razorpay.common.util.RandomizerUtil;
import com.capstone.razorpay.payment.processor.PaymentProcessor;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {



    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";
        String bankCode = request.methodDetails() != null ? request.methodDetails().get("BANK").toString() : null;
        if(BANK_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponse.Failure("BANK_REJECTED",
                    "Bank rejected the transaction registration");
        }

        String processorReference = "NBK_PROCESSOR" + RandomizerUtil.randomBase64(16);

        String redirectReference = "http://REDIRECT_BANK.com/" + processorReference;

        return new PaymentProcessorResponse.Success(processorReference, redirectReference);
    }
}
