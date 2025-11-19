package com.example.dealsystem.service.deals;

import com.example.dealsystem.domain.AccumulativeDealCount;
import com.example.dealsystem.domain.CurrencyCode;
import com.example.dealsystem.domain.InvalidDeal;
import com.example.dealsystem.domain.ValidDeal;
import com.example.dealsystem.dto.DealDto;
import com.example.dealsystem.repository.AccumulativeDealCountRepository;
import com.example.dealsystem.repository.InvalidDealRepository;
import com.example.dealsystem.repository.ValidDealRepository;
import com.example.dealsystem.validation.DealValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for processing and managing deals
 */
@Service
public class DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealService.class);

    private final ValidDealRepository validDealRepository;
    private final InvalidDealRepository invalidDealRepository;
    private final AccumulativeDealCountRepository accumulativeDealCountRepository;
    private final DealValidator dealValidator;

    public DealService(ValidDealRepository validDealRepository,
                       InvalidDealRepository invalidDealRepository,
                       AccumulativeDealCountRepository accumulativeDealCountRepository,
                       DealValidator dealValidator) {
        this.validDealRepository = validDealRepository;
        this.invalidDealRepository = invalidDealRepository;
        this.accumulativeDealCountRepository = accumulativeDealCountRepository;
        this.dealValidator = dealValidator;
    }

    /**
     * Process a deal: validate, check for duplicates, and save to appropriate table
     * No rollback - all deals are saved (either as valid or invalid)
     * 
     * @param dealDto the deal data
     * @param fileName the source file name
     * @param rowData the original CSV row data
     */
    @Transactional
    public void processDeal(DealDto dealDto, String fileName, String rowData) {
        logger.debug("Processing deal ID: {} from file: {}", dealDto.getDealId(), fileName);
        
        // Check for duplicate deal ID
        if (validDealRepository.existsByDealId(dealDto.getDealId())) {
            logger.warn("Duplicate deal ID detected: {} from file: {}", dealDto.getDealId(), fileName);
            InvalidDeal invalidDeal = createInvalidDeal(dealDto, fileName, rowData, 
                "Deal ID already exists: " + dealDto.getDealId());
            invalidDealRepository.save(invalidDeal);
            logger.info("Saved duplicate deal as invalid: {}", dealDto.getDealId());
            return;
        }

        // Validate deal structure
        DealValidator.ValidationResult validationResult = dealValidator.validate(dealDto);
        
        if (validationResult.isValid()) {
            // Save valid deal
            try {
                ValidDeal validDeal = ValidDeal.valueOf(dealDto);
                validDeal.setFileName(fileName);
                validDealRepository.save(validDeal);
                logger.info("Successfully saved valid deal: {} from file: {}", dealDto.getDealId(), fileName);
            } catch (Exception e) {
                logger.error("Error saving valid deal {}: {}", dealDto.getDealId(), e.getMessage(), e);
                // Save as invalid due to processing error
                InvalidDeal invalidDeal = createInvalidDeal(dealDto, fileName, rowData, 
                    "Error processing deal: " + e.getMessage());
                invalidDealRepository.save(invalidDeal);
            }
        } else {
            // Save invalid deal with validation error
            logger.warn("Deal validation failed for {}: {}", dealDto.getDealId(), validationResult.getErrorMessage());
            InvalidDeal invalidDeal = createInvalidDeal(dealDto, fileName, rowData, validationResult.getErrorMessage());
            invalidDealRepository.save(invalidDeal);
            logger.info("Saved invalid deal: {} - {}", dealDto.getDealId(), validationResult.getErrorMessage());
        }
    }

    /**
     * Update accumulative deal counts per currency for the given file
     * 
     * @param fileName the file name to process
     */
    @Transactional
    public void updateAccumulativeCounts(String fileName) {
        logger.info("Updating accumulative deal counts for file: {}", fileName);
        
        // Count deals per currency from the file
        Map<CurrencyCode, Long> currencyCounts = new HashMap<>();
        
        List<ValidDeal> deals = validDealRepository.findByFileName(fileName);
        logger.debug("Found {} valid deals in file: {}", deals.size(), fileName);
        
        for (ValidDeal deal : deals) {
            CurrencyCode currency = deal.getFromCurrency();
            currencyCounts.put(currency, currencyCounts.getOrDefault(currency, 0L) + 1);
        }

        // Update accumulative counts
        for (Map.Entry<CurrencyCode, Long> entry : currencyCounts.entrySet()) {
            CurrencyCode currency = entry.getKey();
            Long count = entry.getValue();
            
            AccumulativeDealCount accumulativeCount = accumulativeDealCountRepository
                .findByCurrencyCode(currency)
                .orElse(new AccumulativeDealCount(currency));
            
            if (accumulativeCount.getId() == null) {
                accumulativeCount.setCountOfDeals(count);
                accumulativeDealCountRepository.save(accumulativeCount);
                logger.info("Created new accumulative count for {}: {}", currency, count);
            } else {
                long previousCount = accumulativeCount.getCountOfDeals();
                accumulativeCount.increment(count);
                accumulativeDealCountRepository.save(accumulativeCount);
                logger.info("Updated accumulative count for {}: {} -> {}", 
                    currency, previousCount, accumulativeCount.getCountOfDeals());
            }
        }
        
        logger.info("Completed updating accumulative counts for file: {}", fileName);
    }

    private InvalidDeal createInvalidDeal(DealDto dealDto, String fileName, String rowData, String errorMessage) {
        InvalidDeal invalidDeal = new InvalidDeal();
        invalidDeal.setFileName(fileName);
        invalidDeal.setDealId(dealDto.getDealId());
        invalidDeal.setFromCurrency(dealDto.getFromCurrency());
        invalidDeal.setToCurrency(dealDto.getToCurrency());
        invalidDeal.setDateTime(dealDto.getDateTime());
        invalidDeal.setAmount(dealDto.getAmount());
        invalidDeal.setErrorMessage(errorMessage);
        invalidDeal.setRowData(rowData);
        return invalidDeal;
    }
}

