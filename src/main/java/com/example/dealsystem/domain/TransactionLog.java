package com.example.dealsystem.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_log", indexes = {
    @Index(name = "idx_file_name", columnList = "file_name", unique = true)
})
@Getter
@Setter
public class TransactionLog extends AbstractDomain {

    @Column(name = "file_name", nullable = false, unique = true, length = 255)
    private String fileName;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "valid_count")
    private Long validCount;

    @Column(name = "invalid_count")
    private Long invalidCount;

    @Column(name = "processing_duration_ms")
    private Long processingDurationMs;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    public TransactionLog() {
        this.status = TransactionStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
    }

    public TransactionLog(String fileName) {
        this();
        this.fileName = fileName;
    }

    public enum TransactionStatus {
        PROCESSING, COMPLETED, FAILED
    }
}

