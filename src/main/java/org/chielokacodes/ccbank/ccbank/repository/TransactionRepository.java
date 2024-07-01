package org.chielokacodes.ccbank.ccbank.repository;

import org.chielokacodes.ccbank.ccbank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {


    List<Transaction> findAllBySourceAccountNumberAndCreatedAtBetween(String accountNumber,  LocalDate startDate, LocalDate endDate);

    List<Transaction> findAllBySourceAccountNumber(String accountNumber);
}
