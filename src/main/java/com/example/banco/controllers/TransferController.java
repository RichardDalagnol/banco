package com.example.banco.controllers;

import com.example.banco.models.Idempotency;
import com.example.banco.models.Transfer;
import com.example.banco.dtos.TransferInput;
import com.example.banco.services.TransferService;
import com.example.banco.dtos.TransferView;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class TransferController {
    private final TransferService transfers;

    public TransferController(TransferService service) {
        this.transfers = service;
    }

    @PostMapping("/transferencias")
    public TransferView transfer(@RequestHeader("Idempotency-Key") String key,
                                 @Valid @RequestBody TransferInput input) {
        return transfers.transfer(input.fromAccountId(),input.toAccountId(),input.amount(),key);
    }
}