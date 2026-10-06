package com.example.banco.repositories;

import com.example.banco.models.Transfer;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer,Long> {
    @Query("""
        select t from Transfer t
        join fetch t.debitMovement
        join fetch t.creditMovement
        where t.idempotencyKey=:key
        """)
    Optional<Transfer> findResultByKey(@Param("key") String key);
}

