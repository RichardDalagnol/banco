package com.example.banco.models;

import com.example.banco.dtos.MovementView;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Immutable;
import org.hibernate.generator.EventType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name="movement",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idempotency_key","operation_type"}))
@Immutable
@Getter
public class Movement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="account_id",nullable=false,updatable=false)
    private Account account;

    @Enumerated(EnumType.STRING) @Column(name="operation_type", nullable=false, length=20, updatable=false)
    private OperationType type;

    @Column(nullable=false,precision = 19, scale=2, updatable=false)
    private BigDecimal amount;

    @Column(name="balance_after", nullable=false, precision=19,scale=2,updatable=false)
    private BigDecimal balanceAfter;

    @Column(name="idempotency_key", nullable=false, length=100, updatable=false)
    private String idempotencyKey;

    @Generated(event=EventType.INSERT)
    @Column(name="created_at", nullable=false, insertable=false, updatable=false)
    private OffsetDateTime createdAt;

    protected Movement() {}
    public Movement(Account account,OperationType type,BigDecimal amount,BigDecimal balanceAfter,String key) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.idempotencyKey = key;
    }
    public MovementView toView() {
        return new MovementView(id,account.getId(),type,amount,balanceAfter,idempotencyKey,createdAt);
    }
}

