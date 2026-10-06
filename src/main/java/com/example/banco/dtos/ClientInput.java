package com.example.banco.dtos;

import jakarta.validation.constraints.*;

public record ClientInput(@NotBlank @Size(max = 120) String name,
                          @NotBlank @Pattern(regexp = "([0-9]{11}|[A-Z0-9]{12}[0-9]{2})") String document,
                          @Email @Size(max = 254) String email) {
}