package com.example.banco.services;

import com.example.banco.models.Account;
import com.example.banco.repositories.AccountRepository;
import com.example.banco.exceptions.BusinessException;
import com.example.banco.models.Movement;
import com.example.banco.repositories.MovementRepository;
import com.example.banco.dtos.StatementView;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import java.math.BigDecimal;
import java.time.*;

@Service
public class StatementService {
    private final AccountRepository accounts;
    private final MovementRepository movements;
    private final Clock clock;

    public StatementService(AccountRepository accounts, MovementRepository movements, Clock clock) {
        this.accounts = accounts;
        this.movements = movements;
        this.clock = clock;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public StatementView statement(Long id, LocalDate startDate, LocalDate endDate) {
        LocalDate end = endDate == null ? LocalDate.now(clock) : endDate;
        LocalDate start = startDate == null ? end.withDayOfMonth(1) : startDate;
        validate(start, end);

        Account account = accounts.findById(id).orElseThrow(BusinessException::missing);
        OffsetDateTime from = start.atStartOfDay(clock.getZone()).toOffsetDateTime();
        OffsetDateTime until = end.plusDays(1).atStartOfDay(clock.getZone()).toOffsetDateTime();
        var entries = movements.period(id, from, until);

        return new StatementView(account.getId(), account.getAccountNumber(), account.getCheckDigit(),
                account.getBalance(), start, end, balanceBefore(id, from), balanceBefore(id, until), entries);
    }

    private static void validate(LocalDate start, LocalDate end) {
        if (start.isAfter(end))
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_PERIOD", "Data inicial deve ser anterior ou igual à data final");
        if (start.getYear() < 1 || end.getYear() > 9999)
            throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_PERIOD", "Datas devem estar entre os anos 1 e 9999");
    }

    private BigDecimal balanceBefore(Long id, OffsetDateTime instant) {
        return movements.findFirstByAccount_IdAndCreatedAtBeforeOrderByCreatedAtDescIdDesc(id, instant)
                .map(Movement::getBalanceAfter).orElse(BigDecimal.ZERO.setScale(2));
    }
}

