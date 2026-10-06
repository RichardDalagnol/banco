package com.example.banco.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;


import java.math.BigDecimal;

@Entity
@Table(name = "account", uniqueConstraints = {
        @UniqueConstraint(name = "account_number_digit_unique",
                columnNames = {"account_number", "check_digit"})
})
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, updatable = false)
    private Long accountNumber;

    @Column(name = "check_digit", nullable = false,  updatable = false)
    private Integer checkDigit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false, updatable = false)
    private Client client;

    @Column(nullable = false, length = 80)
    private String label;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO.setScale(2);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountStatus status = AccountStatus.ACTIVE;

    public Account() {
    }
}