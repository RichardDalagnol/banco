package com.example.banco.repositories;

import com.example.banco.models.Account;
import com.example.banco.models.AccountStatus;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id=:id")
    Optional<Account> locked(Long id);

    @Query("select count(a) > 0 from Account a where a.client.id = :clientId")
    boolean existsByclientId(Long clientId);
    @Query("select count(a) > 0 from Account a where a.client.id = :clientId and a.status = :status")
    boolean existsByclientIdAndStatus(Long clientId, AccountStatus status);
}
