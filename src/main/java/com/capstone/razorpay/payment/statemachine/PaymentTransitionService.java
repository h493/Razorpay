package com.capstone.razorpay.payment.statemachine;

import com.capstone.razorpay.common.enums.PaymentActor;
import com.capstone.razorpay.common.enums.PaymentEvent;
import com.capstone.razorpay.common.enums.PaymentStatus;
import com.capstone.razorpay.payment.entity.Payment;
import com.capstone.razorpay.payment.entity.PaymentTransitionLog;
import com.capstone.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM) //TODO : Fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();

        paymentTransitionLogRepository.save(log);
        payment.setStatus(next);
        return next;
    }
}
