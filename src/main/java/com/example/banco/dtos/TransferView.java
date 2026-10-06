package com.example.banco.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransferView(Long id, Long fromAccountId, Long toAccountId, BigDecimal amount,
                           BigDecimal fromBalanceAfter, BigDecimal toBalanceAfter,
                           Long debitMovementId, Long creditMovementId, String idempotencyKey,
                           OffsetDateTime createdAt) {
}
