package com.example.banco.services;

import com.example.banco.models.Account;
import com.example.banco.dtos.AccountInput;
import com.example.banco.repositories.AccountRepository;
import com.example.banco.models.AccountStatus;
import com.example.banco.dtos.AccountUpdate;
import com.example.banco.dtos.AccountView;
import com.example.banco.exceptions.BusinessException;
import com.example.banco.models.Client;
import com.example.banco.repositories.ClientRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

@Service
@Transactional
public class AccountService {
    private final AccountRepository accounts;
    private final ClientRepository clients;

    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository) {
        accounts = accountRepository;
        clients = clientRepository;
    }

    public AccountView create(AccountInput input) {
        Client c = clients.findById(input.clientId()).orElseThrow(BusinessException::missing);
        if (!c.isActive()) throw BusinessException.conflict("INACTIVE_CUSTOMER", "Cliente inativo");
        Account a = new Account();
        a.setClient(c);
        a.setLabel(input.label());
        a.setAccountNumber(input.accountNumber());
        a.setCheckDigit(input.digit());
        return AccountView.of(accounts.save(a));
    }

    @Transactional(readOnly = true)
    public AccountView get(Long id) {
        return AccountView.of(accounts.findById(id).orElseThrow(BusinessException::missing));
    }

    @Transactional(readOnly = true)
    public Page<AccountView> list(Pageable p) {
        return accounts.findAll(p).map(AccountView::of);
    }

    public AccountView update(Long id, AccountUpdate input) {
        Account a = findById(id);
        active(a);
        a.setLabel(input.label());
        return AccountView.of(a);
    }

    public void delete(Long id) {
        Account a = findById(id);
        if (a.getBalance().signum() != 0)
            throw BusinessException.conflict("NON_ZERO_BALANCE", "Saldo deve ser zero para encerrar");
        a.setStatus(AccountStatus.CLOSED);
    }

    private Account findById(Long id) {
        return accounts.findById(id).orElseThrow(BusinessException::missing);
    }

    public static void active(Account a) {
        if (a.getStatus() != AccountStatus.ACTIVE) throw BusinessException.conflict("CLOSED_ACCOUNT", "Conta encerrada");
    }
}
