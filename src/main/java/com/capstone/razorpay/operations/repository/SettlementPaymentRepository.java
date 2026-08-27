package com.capstone.razorpay.operations.repository;

import com.capstone.razorpay.operations.entity.SettlementPayment;
import com.capstone.razorpay.operations.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
}
