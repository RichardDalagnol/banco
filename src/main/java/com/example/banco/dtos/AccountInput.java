package com.example.banco.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountInput(
        @NotNull Long accountNumber,
        @NotNull Integer digit,
        @NotNull Long clientId,
        @NotBlank @Size(max = 80) String label) {
}
