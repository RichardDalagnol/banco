package com.example.banco.controllers;

import com.example.banco.dtos.ClientInput;
import com.example.banco.services.ClientService;
import com.example.banco.dtos.ClientView;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;

@RestController
@RequestMapping("/api")
@Validated
public class ClientController {
    private final ClientService clients;

    public ClientController(ClientService service) {
        this.clients = service;
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientView createClient(@Valid @RequestBody ClientInput i) {
        return clients.create(i);
    }

    @GetMapping("/clientes")
    public Page<ClientView> clients(@RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return clients.list(PageRequest.of(page, size, Sort.by("id")));
    }

    @GetMapping("/clientes/{id}")
    public ClientView client(@PathVariable Long id) {
        return clients.get(id);
    }

    @PutMapping("/clientes/{id}")
    public ClientView updateClient(@PathVariable Long id, @Valid @RequestBody ClientInput i) {
        return clients.update(id, i);
    }

    @DeleteMapping("/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clients.delete(id);
    }
}