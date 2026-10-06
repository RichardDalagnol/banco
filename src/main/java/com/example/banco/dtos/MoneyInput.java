package com.example.banco.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public record MoneyInput(@NotNull @DecimalMin("0.01")
                         @Digits(integer = 17, fraction = 2) BigDecimal amount) {
}
