package com.example.banco.dtos;

import com.example.banco.models.Account;
import com.example.banco.models.AccountStatus;
import com.example.banco.models.Client;

import java.math.BigDecimal;

public record AccountView(Long id, Client client, Long accountNumber, Integer checkDigit,
                          String label, BigDecimal balance, AccountStatus status) {
    public static AccountView of(Account a) {
        return new AccountView(a.getId(),
                a.getClient(),
                a.getAccountNumber(),
                a.getCheckDigit(),
                a.getLabel(),
                a.getBalance(),
                a.getStatus());
    }
}
