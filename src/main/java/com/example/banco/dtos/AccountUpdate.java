package com.example.banco.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountUpdate(@NotBlank @Size(max = 80) String label) {
}
