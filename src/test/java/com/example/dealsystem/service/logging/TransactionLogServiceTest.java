package com.example.dealsystem.service.logging;

import com.example.dealsystem.domain.TransactionLog;
import com.example.dealsystem.repository.InvalidDealRepository;
import com.example.dealsystem.repository.TransactionLogRepository;
import com.example.dealsystem.repository.ValidDealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionLogServiceTest {

    @Mock
    private TransactionLogRepository transactionLogRepository;

    @Mock
    private ValidDealRepository validDealRepository;

    @Mock
    private InvalidDealRepository invalidDealRepository;

    @InjectMocks
    private TransactionLogService transactionLogService;

    private TransactionLog transactionLog;
    private String fileName;

    @BeforeEach
    void setUp() {
        fileName = "test_deals.csv";
        transactionLog = new TransactionLog(fileName);
        transactionLog.setId(1L);
    }

    @Test
    void testSave() {
        when(transactionLogRepository.save(any(TransactionLog.class))).thenReturn(transactionLog);

        TransactionLog result = transactionLogService.save(transactionLog);

        assertNotNull(result);
        verify(transactionLogRepository, times(1)).save(transactionLog);
    }

    @Test
    void testCompleteTransaction() {
        when(transactionLogRepository.findByFileName(fileName))
            .thenReturn(Optional.of(transactionLog));
        when(validDealRepository.countByFileName(fileName)).thenReturn(10L);
        when(invalidDealRepository.countByFileName(fileName)).thenReturn(2L);
        when(transactionLogRepository.save(any(TransactionLog.class))).thenReturn(transactionLog);

        transactionLogService.completeTransaction(fileName);

        assertEquals(10L, transactionLog.getValidCount());
        assertEquals(2L, transactionLog.getInvalidCount());
        assertEquals(TransactionLog.TransactionStatus.COMPLETED, transactionLog.getStatus());
        assertNotNull(transactionLog.getCompletedAt());
        verify(transactionLogRepository, times(1)).save(transactionLog);
    }

    @Test
    void testFailTransaction() {
        String errorMessage = "Test error";
        when(transactionLogRepository.findByFileName(fileName))
            .thenReturn(Optional.of(transactionLog));
        when(transactionLogRepository.save(any(TransactionLog.class))).thenReturn(transactionLog);

        transactionLogService.failTransaction(fileName, errorMessage);

        assertEquals(TransactionLog.TransactionStatus.FAILED, transactionLog.getStatus());
        assertEquals(errorMessage, transactionLog.getErrorMessage());
        assertNotNull(transactionLog.getCompletedAt());
        verify(transactionLogRepository, times(1)).save(transactionLog);
    }

    @Test
    void testIsFileAlreadyImported() {
        when(transactionLogRepository.existsByFileName(fileName)).thenReturn(true);

        boolean result = transactionLogService.isFileAlreadyImported(fileName);

        assertTrue(result);
        verify(transactionLogRepository, times(1)).existsByFileName(fileName);
    }

    @Test
    void testFindByFileName() {
        when(transactionLogRepository.findByFileName(fileName))
            .thenReturn(Optional.of(transactionLog));

        TransactionLog result = transactionLogService.findByFileName(fileName);

        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
    }

    @Test
    void testFindByFileName_NotFound() {
        when(transactionLogRepository.findByFileName(fileName))
            .thenReturn(Optional.empty());

        TransactionLog result = transactionLogService.findByFileName(fileName);

        assertNull(result);
    }
}

