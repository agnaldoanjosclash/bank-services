package com.home.account.repository;

import com.home.account.data.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findByTransactionId(String transactionId);
}
