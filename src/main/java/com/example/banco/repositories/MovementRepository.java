package com.example.banco.repositories;

import com.example.banco.models.Account;
import com.example.banco.models.Movement;
import com.example.banco.dtos.MovementView;
import com.example.banco.models.OperationType;
import com.example.banco.models.Transfer;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.time.OffsetDateTime;
import java.util.*;

public interface MovementRepository extends JpaRepository<Movement,Long> {
    Optional<Movement> findByIdempotencyKeyAndTypeIn(String key,Collection<OperationType> types);

    @Query(value="select m from Movement m where m.account.id=:account order by m.createdAt,m.id",
           countQuery="select count(m) from Movement m where m.account.id=:account")
    Page<Movement> statement(@Param("account") Long account,Pageable pageable);

    @Query("""
        select new com.example.banco.dtos.MovementView(
            m.id, m.account.id, m.type, m.amount, m.balanceAfter, m.idempotencyKey, m.createdAt,
            destination.id, destination.accountNumber, destination.checkDigit)
        from Movement m
        left join Transfer t on t.debitMovement.id = m.id or t.creditMovement.id = m.id
        left join t.creditMovement credit
        left join credit.account destination
        where m.account.id=:account and m.createdAt>=:from and m.createdAt<:until
        order by m.createdAt,m.id
        """)
    List<MovementView> period(@Param("account") Long account, @Param("from") OffsetDateTime from,
                              @Param("until") OffsetDateTime until);

    Optional<Movement> findFirstByAccount_IdAndCreatedAtBeforeOrderByCreatedAtDescIdDesc(
        Long accountId,OffsetDateTime instant);
}

