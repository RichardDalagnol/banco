package com.example.banco.dtos;

import com.example.banco.models.Client;


public record ClientView(Long id, String name, String document, String email, boolean active) {
    public static ClientView of(Client c) {
        return new ClientView(c.getId(),
                c.getName(),
                c.getDocument(),
                c.getEmail(),
                c.isActive());
    }
}
