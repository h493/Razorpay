package com.capstone.razorpay.operations.settlement;

import com.capstone.razorpay.common.entity.Money;
import com.capstone.razorpay.operations.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
                                String bankAccount, String ifsc);
}
