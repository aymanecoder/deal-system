package com.example.dealsystem.service.logging;

import com.example.dealsystem.domain.TransactionLog;
import com.example.dealsystem.repository.InvalidDealRepository;
import com.example.dealsystem.repository.TransactionLogRepository;
import com.example.dealsystem.repository.ValidDealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Service for managing transaction logs
 */
@Service
public class TransactionLogService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLogService.class);

    private final TransactionLogRepository transactionLogRepository;
    private final ValidDealRepository validDealRepository;
    private final InvalidDealRepository invalidDealRepository;

    public TransactionLogService(TransactionLogRepository transactionLogRepository,
                                 ValidDealRepository validDealRepository,
                                 InvalidDealRepository invalidDealRepository) {
        this.transactionLogRepository = transactionLogRepository;
        this.validDealRepository = validDealRepository;
        this.invalidDealRepository = invalidDealRepository;
    }

    /**
     * Save a transaction log entry
     */
    @Transactional
    public TransactionLog save(TransactionLog transactionLog) {
        logger.debug("Saving transaction log for file: {}", transactionLog.getFileName());
        TransactionLog saved = transactionLogRepository.save(transactionLog);
        logger.info("Saved transaction log with ID: {} for file: {}", 
            saved.getId(), saved.getFileName());
        return saved;
    }

    /**
     * Mark transaction as completed and update statistics
     */
    @Transactional
    public void completeTransaction(String fileName) {
        logger.info("Completing transaction for file: {}", fileName);
        
        TransactionLog transactionLog = transactionLogRepository.findByFileName(fileName)
            .orElseThrow(() -> {
                logger.error("Transaction log not found for file: {}", fileName);
                return new IllegalStateException("Transaction log not found for file: " + fileName);
            });

        Long validCount = validDealRepository.countByFileName(fileName);
        Long invalidCount = invalidDealRepository.countByFileName(fileName);
        
        logger.debug("File {} statistics - Valid: {}, Invalid: {}", fileName, validCount, invalidCount);

        transactionLog.setValidCount(validCount);
        transactionLog.setInvalidCount(invalidCount);
        transactionLog.setCompletedAt(LocalDateTime.now());
        
        if (transactionLog.getStartedAt() != null) {
            Duration duration = Duration.between(transactionLog.getStartedAt(), transactionLog.getCompletedAt());
            transactionLog.setProcessingDurationMs(duration.toMillis());
            logger.debug("Processing duration for file {}: {}ms", fileName, duration.toMillis());
        }
        
        transactionLog.setStatus(TransactionLog.TransactionStatus.COMPLETED);
        
        transactionLogRepository.save(transactionLog);
        logger.info("Transaction completed for file: {} - Valid: {}, Invalid: {}, Duration: {}ms", 
            fileName, validCount, invalidCount, transactionLog.getProcessingDurationMs());
    }

    /**
     * Mark transaction as failed with error message
     */
    @Transactional
    public void failTransaction(String fileName, String errorMessage) {
        logger.error("Failing transaction for file: {} - Error: {}", fileName, errorMessage);
        
        TransactionLog transactionLog = transactionLogRepository.findByFileName(fileName)
            .orElseThrow(() -> {
                logger.error("Transaction log not found for file: {}", fileName);
                return new IllegalStateException("Transaction log not found for file: " + fileName);
            });

        transactionLog.setStatus(TransactionLog.TransactionStatus.FAILED);
        transactionLog.setErrorMessage(errorMessage);
        transactionLog.setCompletedAt(LocalDateTime.now());
        
        if (transactionLog.getStartedAt() != null) {
            Duration duration = Duration.between(transactionLog.getStartedAt(), transactionLog.getCompletedAt());
            transactionLog.setProcessingDurationMs(duration.toMillis());
            logger.debug("Failed transaction duration for file {}: {}ms", fileName, duration.toMillis());
        }
        
        transactionLogRepository.save(transactionLog);
        logger.error("Transaction failed for file: {} - Error: {}", fileName, errorMessage);
    }

    /**
     * Check if a file has already been imported
     */
    public boolean isFileAlreadyImported(String fileName) {
        boolean exists = transactionLogRepository.existsByFileName(fileName);
        if (exists) {
            logger.debug("File {} already exists in transaction log", fileName);
        }
        return exists;
    }

    /**
     * Find transaction log by file name
     */
    public TransactionLog findByFileName(String fileName) {
        logger.debug("Searching for transaction log for file: {}", fileName);
        return transactionLogRepository.findByFileName(fileName)
            .orElse(null);
    }
}

