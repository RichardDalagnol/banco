package com.example.banco.services;

import com.example.banco.models.Account;
import com.example.banco.repositories.AccountRepository;
import com.example.banco.exceptions.BusinessException;
import com.example.banco.models.Idempotency;
import com.example.banco.repositories.IdempotencyRepository;


import com.example.banco.models.OperationType;
import com.example.banco.models.Transfer;
import com.example.banco.repositories.TransferRepository;
import com.example.banco.dtos.TransferView;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final AccountRepository accounts;
    private final MovementService movements;
    private final TransferRepository transfers;
    private final IdempotencyRepository idempotency;

    public TransferService(AccountRepository accounts, MovementService movements, TransferRepository transfers, IdempotencyRepository idempotency) {
        this.accounts = accounts;
        this.movements = movements;
        this.transfers = transfers;
        this.idempotency = idempotency;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransferView transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String key) {
        amount = MoneyValidation.normalize(amount, key);
        validate(fromAccountId, toAccountId);

        if (idempotency.reserve(key, fromAccountId, OperationType.TRANSFER.name(), amount, toAccountId) == 0) {
            Idempotency reservation = idempotency.findById(key).orElseThrow(BusinessException::missing);
            if (!reservation.matches(fromAccountId, OperationType.TRANSFER, amount, toAccountId))
                throw BusinessException.conflict("IDEMPOTENCY_CONFLICT", "Chave já utilizada com outro payload ou operação");
            return transfers.findResultByKey(key).orElseThrow(BusinessException::missing).toView();
        }

        Account first = accounts.locked(Math.min(fromAccountId, toAccountId)).orElseThrow(BusinessException::missing);
        Account second = accounts.locked(Math.max(fromAccountId, toAccountId)).orElseThrow(BusinessException::missing);
        Account from = first.getId().equals(fromAccountId) ? first : second;
        Account to = first.getId().equals(toAccountId) ? first : second;
        AccountService.active(from);
        AccountService.active(to);
        var debit = movements.withdraw(from, amount, key, OperationType.TRANSFER_OUT);
        var credit = movements.deposit(to, amount, key, OperationType.TRANSFER_IN);
        return transfers.save(new Transfer(key, debit, credit)).toView();
    }

    private static void validate(Long fromAccountId, Long toAccountId) {
        if (fromAccountId == null || toAccountId == null )
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_ACCOUNT", "IDs das contas devem ser positivos");
        if (fromAccountId.equals(toAccountId))
            throw new BusinessException(HttpStatus.BAD_REQUEST, "SAME_ACCOUNT", "Origem e destino devem ser diferentes");
    }
}
