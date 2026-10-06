package com.example.banco.services;

import com.example.banco.exceptions.BusinessException;
import com.example.banco.models.Idempotency;

import org.springframework.http.HttpStatus;
import java.math.*;

final class MoneyValidation {
    private MoneyValidation() {}

    static BigDecimal normalize(BigDecimal amount, String key) {
        if (key == null || !key.matches("[A-Za-z0-9._:-]{1,100}"))
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_KEY",
                    "Idempotency-Key deve ter 1 a 100 caracteres ASCII: letras, números, ponto, _, : ou -");
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2)
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_AMOUNT", "Valor inválido");
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }
}
