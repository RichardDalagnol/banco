package com.example.banco.models;

import com.example.banco.dtos.TransferView;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Immutable;
import org.hibernate.generator.EventType;
import java.time.OffsetDateTime;

@Entity @Table(name="transfer") @Immutable
@Getter
public class Transfer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="idempotency_key",nullable=false,unique=true,length=100,updatable=false)
    private String idempotencyKey;

    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="debit_movement_id",nullable=false,unique=true,updatable=false)
    private Movement debitMovement;

    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="credit_movement_id",nullable=false,unique=true,updatable=false)
    private Movement creditMovement;

    @Generated(event=EventType.INSERT)
    @Column(name="created_at",nullable=false,insertable=false,updatable=false)
    private OffsetDateTime createdAt;

    protected Transfer() {}
    public Transfer(String key,Movement debitMovement,Movement creditMovement) {
        this.idempotencyKey=key;this.debitMovement=debitMovement;this.creditMovement=creditMovement;
    }
    public TransferView toView() {
        var debit = debitMovement.toView();
        var credit = creditMovement.toView();
        return new TransferView(id, debit.accountId(), credit.accountId(),debit.amount(),
            debit.balanceAfter(),credit.balanceAfter(),debit.id(),credit.id(),idempotencyKey,createdAt);
    }
}

