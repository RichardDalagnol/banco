package com.example.banco.controllers;

import com.example.banco.models.Account;
import com.example.banco.dtos.AccountInput;
import com.example.banco.services.AccountService;
import com.example.banco.dtos.AccountUpdate;
import com.example.banco.dtos.AccountView;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;

@RestController
@RequestMapping("/api")
@Validated
public class AccountController {
    private final AccountService accounts;

    public AccountController(AccountService service) {
        this.accounts = service;
    }

    @PostMapping("/contas")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountView createAccount(@Valid @RequestBody AccountInput i) {
        return accounts.create(i);
    }

    @GetMapping("/contas")
    public Page<AccountView> accounts(@RequestParam(defaultValue = "0") @Min(0) int page,
                                      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return accounts.list(PageRequest.of(page, size, Sort.by("id")));
    }

    @GetMapping("/contas/{id}")
    public AccountView account(@PathVariable Long id) {
        return accounts.get(id);
    }

    @PutMapping("/contas/{id}")
    public AccountView updateAccount(@PathVariable Long id,
                                     @Valid @RequestBody AccountUpdate i) {
        return accounts.update(id, i);
    }

    @DeleteMapping("/contas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        accounts.delete(id);
    }
}