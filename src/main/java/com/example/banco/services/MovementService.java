package com.example.banco.services;

import com.example.banco.models.Account;
import com.example.banco.repositories.AccountRepository;
import com.example.banco.exceptions.BusinessException;
import com.example.banco.models.Idempotency;
import com.example.banco.repositories.IdempotencyRepository;
import com.example.banco.models.Movement;
import com.example.banco.repositories.MovementRepository;
import com.example.banco.dtos.MovementView;
import com.example.banco.models.OperationType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.math.*;
import java.util.List;

@Service
public class MovementService {
    private final AccountRepository accounts;
    private final MovementRepository movements;
    private final IdempotencyRepository idempotency;

    public MovementService(AccountRepository accounts, MovementRepository movements, IdempotencyRepository idempotency) {
        this.accounts = accounts;
        this.movements = movements;
        this.idempotency = idempotency;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public MovementView deposit(Long accountId, BigDecimal amount, String key) {
        amount = validateAndNormalize(amount, key);
        MovementView previous = reserveOrReplay(accountId, OperationType.DEPOSIT, amount, key);
        if (previous != null) return previous;

        Account account = lockActiveAccount(accountId);
        return deposit(account, amount, key, OperationType.DEPOSIT).toView();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public MovementView withdraw(Long accountId, BigDecimal amount, String key) {
        amount = validateAndNormalize(amount, key);
        MovementView previous = reserveOrReplay(accountId, OperationType.WITHDRAWAL, amount, key);
        if (previous != null) return previous;

        Account account = lockActiveAccount(accountId);
        return withdraw(account, amount, key, OperationType.WITHDRAWAL).toView();
    }

    @Transactional()
    Movement deposit(Account account, BigDecimal amount, String key, OperationType type) {
        amount = validateAndNormalize(amount, key);
        AccountService.active(account);
        BigDecimal balance = account.getBalance().add(amount);
        return saveMovement(account, type, amount, balance, key);
    }

    @Transactional()
    Movement withdraw(Account account, BigDecimal amount, String key, OperationType type) {
        amount = validateAndNormalize(amount, key);
        AccountService.active(account);
        if (account.getBalance().compareTo(amount) < 0)
            throw BusinessException.conflict("INSUFFICIENT_FUNDS", "Saldo insuficiente");
        return saveMovement(account, type, amount, account.getBalance().subtract(amount), key);
    }


    private BigDecimal validateAndNormalize(BigDecimal amount, String key) {
        return MoneyValidation.normalize(amount, key);
    }

    private MovementView reserveOrReplay(Long accountId, OperationType type, BigDecimal amount, String key) {
        if (idempotency.reserve(key, accountId, type.name(), amount, null) == 1) {
            return null;
        }

        Idempotency reservation = idempotency.findById(key)
                .orElseThrow(BusinessException::missing);
        if (!reservation.matches(accountId, type, amount, null)) {
            throw BusinessException.conflict("IDEMPOTENCY_CONFLICT",
                    "Chave já utilizada com outro payload ou operação");
        }
        return movements.findByIdempotencyKeyAndTypeIn(key, List.of(type))
                .orElseThrow(BusinessException::missing).toView();
    }

    private Account lockActiveAccount(Long accountId) {
        Account account = accounts.locked(accountId).orElseThrow(BusinessException::missing);
        AccountService.active(account);
        return account;
    }

    private Movement saveMovement(Account account, OperationType type, BigDecimal amount,
                                        BigDecimal balance, String key) {
        account.setBalance(balance);
        accounts.flush();
        return movements.save(new Movement(account, type, amount, balance, key));
    }

    @Transactional(readOnly = true)
    public Page<MovementView> statement(Long id, int page, int size) {
        if (!accounts.existsById(id)) throw BusinessException.missing();
        return movements.statement(id, PageRequest.of(page, size))
                .map(Movement::toView);
    }
}
