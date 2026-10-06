package com.example.banco.repositories;

import com.example.banco.models.Idempotency;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

public interface IdempotencyRepository extends JpaRepository<Idempotency,String> {
    @Transactional
    @Modifying
    @Query(value="""
        INSERT INTO idempotency(key,account_id,operation_type,amount,destination_account_id)
        VALUES (:key,:accountId,:type,:amount,:destination)
        ON CONFLICT (key) DO NOTHING
        """,nativeQuery=true)
    int reserve(@Param("key") String key,@Param("accountId") Long accountId,
                @Param("type") String type,@Param("amount") BigDecimal amount,
                @Param("destination") Long destination);
}

