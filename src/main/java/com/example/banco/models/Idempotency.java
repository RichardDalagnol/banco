package com.example.banco.models;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity @Table(name="idempotency") @Immutable
@Getter
public class Idempotency {
    @Id @Column(name="key",length=100)
    private String key;
    @Column(name="account_id",nullable=false,updatable=false)
    private Long accountId;
    @Column(name="destination_account_id",updatable=false)
    private Long destinationAccountId;
    @Enumerated(EnumType.STRING) @Column(name="operation_type",nullable=false,length=20,updatable=false)
    private OperationType type;
    @Column(nullable=false,precision=19,scale=2,updatable=false)
    private BigDecimal amount;
    @Column(name="created_at",nullable=false,insertable=false,updatable=false)
    private OffsetDateTime createdAt;

    protected Idempotency() {}
    public boolean matches(Long accountId,OperationType type,BigDecimal amount,Long destination) {
        return Objects.equals(this.accountId,accountId) && this.type==type
            && this.amount.compareTo(amount)==0 && Objects.equals(destinationAccountId,destination);
    }
}

