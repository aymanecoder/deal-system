package com.example.dealsystem.service.csv;

import com.example.dealsystem.domain.TransactionLog;
import com.example.dealsystem.dto.DealDto;
import com.example.dealsystem.exception.FileProcessingException;
import com.example.dealsystem.service.deals.DealService;
import com.example.dealsystem.service.logging.TransactionLogService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling CSV file uploads and processing
 */
@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    
    private static final String[] CSV_HEADERS = {"deal_id", "from_currency", "to_currency", "date_time", "amount"};

    private final DealService dealService;
    private final TransactionLogService transactionLogService;

    public FileUploadService(DealService dealService,
                             TransactionLogService transactionLogService) {
        this.dealService = dealService;
        this.transactionLogService = transactionLogService;
    }

    /**
     * Upload and process a CSV file containing deals
     * No rollback - all processed rows are saved to database
     * 
     * @param file the CSV file to process
     * @param transactionLog the transaction log entry
     * @throws FileProcessingException if file processing fails
     */
    @Transactional
    public void uploadFile(MultipartFile file, TransactionLog transactionLog) {
        String fileName = file.getOriginalFilename();
        logger.info("Starting file upload process for file: {} (size: {} bytes)", 
            fileName, file.getSize());

        if (file.isEmpty()) {
            logger.error("Attempted to upload empty file: {}", fileName);
            throw new FileProcessingException("File is empty: " + fileName);
        }

        try {
            int totalRows = 0;
            int processedRows = 0;
            List<String> errors = new ArrayList<>();
            
            // Process CSV file
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                
                CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader(CSV_HEADERS)
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build();

                try (CSVParser parser = new CSVParser(reader, format)) {
                    int rowNumber = 1; // Start from 1 (header is row 0)
                    
                    for (CSVRecord record : parser) {
                        rowNumber++;
                        totalRows++;
                        String rowData = String.join(",", record.values());
                        
                        try {
                            DealDto dealDto = mapToDealDto(record);
                            dealService.processDeal(dealDto, fileName, rowData);
                            processedRows++;
                            logger.debug("Processed row {} successfully", rowNumber);
                        } catch (Exception e) {
                            logger.error("Error processing row {} in file {}: {}", 
                                rowNumber, fileName, e.getMessage(), e);
                            errors.add("Row " + rowNumber + ": " + e.getMessage());
                            // Continue processing - no rollback
                        }
                    }
                }
            }

            logger.info("CSV parsing completed. Total rows: {}, Processed: {}, Errors: {}", 
                totalRows, processedRows, errors.size());

            // Update accumulative counts
            logger.info("Updating accumulative deal counts for file: {}", fileName);
            dealService.updateAccumulativeCounts(fileName);

            // Complete transaction
            transactionLogService.completeTransaction(fileName);
            
            logger.info("File upload completed successfully: {} - Processed {} rows", 
                fileName, processedRows);
            
        } catch (FileProcessingException e) {
            logger.error("File processing exception for file {}: {}", fileName, e.getMessage(), e);
            transactionLogService.failTransaction(fileName, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error processing file {}: {}", fileName, e.getMessage(), e);
            transactionLogService.failTransaction(fileName, "Unexpected error: " + e.getMessage());
            throw new FileProcessingException("Failed to process file: " + fileName, e);
        }
    }

    /**
     * Map CSV record to DealDto
     */
    private DealDto mapToDealDto(CSVRecord record) {
        DealDto dealDto = new DealDto();
        dealDto.setDealId(record.get("deal_id"));
        dealDto.setFromCurrency(record.get("from_currency"));
        dealDto.setToCurrency(record.get("to_currency"));
        dealDto.setDateTime(record.get("date_time"));
        dealDto.setAmount(record.get("amount"));
        return dealDto;
    }
}

