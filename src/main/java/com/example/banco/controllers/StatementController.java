package com.example.banco.controllers;

import com.example.banco.services.StatementService;
import com.example.banco.dtos.StatementView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class StatementController {
    private final StatementService statements;

    public StatementController(StatementService service) {
        this.statements = service;
    }

    @GetMapping("/contas/{id}/extrato")
    public StatementView statement(@PathVariable Long id,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return statements.statement(id, startDate, endDate);
    }
}