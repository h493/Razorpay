package com.capstone.razorpay.payment.repository;


import com.capstone.razorpay.payment.entity.OrderRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {
    boolean existsByMerchantIdAndReceipt(UUID merchantId, String receipt);

    Optional<OrderRecord> findByIdAndMerchantId(UUID orderId, UUID merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from orderRecord o where o.id = :orderId and o.merchantId = :merchantId")
    Optional<OrderRecord> findByIdAndMerchantIdForUpdate(UUID orderId, UUID merchantId);

}
