package com.example.banco.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, unique = true, length = 20)
    private String document;
    @Column(nullable = false, length = 254)
    private String email;
    @Column(nullable = false)
    private boolean active = true;

    public Client() {
    }
}
