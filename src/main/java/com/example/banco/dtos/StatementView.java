package com.example.banco.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record StatementView(Long accountId, Long accountNumber, Integer checkDigit,
                            BigDecimal currentBalance, LocalDate startDate, LocalDate endDate,
                            BigDecimal openingBalance, BigDecimal closingBalance,
                            List<MovementView> movements) {
}
