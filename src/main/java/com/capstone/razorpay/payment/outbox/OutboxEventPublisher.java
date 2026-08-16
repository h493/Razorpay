package com.capstone.razorpay.payment.outbox;

import com.capstone.razorpay.common.enums.EventAggregateType;
import com.capstone.razorpay.payment.entity.OutboxEvent;
import com.capstone.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType aggregateType,
                        UUID aggregateId,
                        String eventType,
                        Map<String, Object> payload){

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .build();
        outboxEventRepository.save(outboxEvent);
    }
}
