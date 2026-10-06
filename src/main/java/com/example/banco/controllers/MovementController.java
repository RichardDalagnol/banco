package com.example.banco.controllers;

import com.example.banco.models.Idempotency;
import com.example.banco.dtos.MoneyInput;
import com.example.banco.services.MovementService;
import com.example.banco.dtos.MovementView;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;

@RestController
@RequestMapping("/api")
@Validated
public class MovementController {
    private final MovementService movements;

    public MovementController(MovementService service) {
        this.movements = service;
    }

    @PostMapping("/contas/{id}/depositos")
    public MovementView deposit(@PathVariable Long id, @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody MoneyInput i) {
        return movements.deposit(id,i.amount(), key);
    }

    @PostMapping("/contas/{id}/saques")
    public MovementView withdraw(@PathVariable Long id, @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody MoneyInput i) {
        return movements.withdraw(id,i.amount(), key);
    }

    @GetMapping("/contas/{id}/movimentacoes")
    public Page<MovementView> pagedStatement(@PathVariable Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return movements.statement(id, page, size);
    }
}