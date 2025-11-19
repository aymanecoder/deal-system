package com.example.dealsystem.repository;

import com.example.dealsystem.domain.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    
    Optional<TransactionLog> findByFileName(String fileName);
    
    boolean existsByFileName(String fileName);
}

