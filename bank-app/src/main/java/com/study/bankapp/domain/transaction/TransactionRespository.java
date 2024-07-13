package com.study.bankapp.domain.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRespository extends JpaRepository<Transaction, Long> {
}
