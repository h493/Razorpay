package com.capstone.razorpay.payment.processor.strategy;

import com.capstone.razorpay.common.util.RandomizerUtil;
import com.capstone.razorpay.payment.processor.PaymentProcessor;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.capstone.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "40000000000002";
    public static final String PAN_CARD_EXPIRED = "4000003200000002";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        String pan = request.pan();

        if(PAN_CARD_DECLINED.equals(pan)){
            log.warn("Card Declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card Declined by Bank");
        }

        if(PAN_CARD_EXPIRED.equals(pan)){
            log.warn("Pan card has expired");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has Expired");
        }

        String processorReference = "CARD_PROCESSOR" + RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponse.Pending(processorReference);
    }
}
