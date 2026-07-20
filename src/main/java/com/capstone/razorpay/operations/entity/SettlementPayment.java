package com.capstone.razorpay.operations.entity;

import com.capstone.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settlement_payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SettlementPayment extends BaseEntity {

    @EmbeddedId
    private SettlementPaymentId id; // composite key PK

    @MapsId("settlementId") // maps to the settlementId field in SettlementPaymentId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
}
