package com.example.banco.dtos;

import com.example.banco.models.OperationType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovementView(Long id, Long accountId, OperationType type, BigDecimal amount,
                           BigDecimal balanceAfter, String idempotencyKey, OffsetDateTime createdAt,
                           Long destinationAccountId, Long destinationAccountNumber,
                           Integer destinationCheckDigit) {
    public MovementView(Long id, Long accountId, OperationType type, BigDecimal amount,
                        BigDecimal balanceAfter, String idempotencyKey, OffsetDateTime createdAt) {
        this(id, accountId, type, amount, balanceAfter, idempotencyKey, createdAt, null, null, null);
    }
}
