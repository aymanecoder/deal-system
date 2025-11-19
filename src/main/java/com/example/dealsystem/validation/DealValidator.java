package com.example.dealsystem.validation;

import com.example.dealsystem.domain.CurrencyCode;
import com.example.dealsystem.dto.DealDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validator for Deal DTOs
 * Validates deal structure, field presence, and data types
 */
@Component
public class DealValidator {

    private static final Logger logger = LoggerFactory.getLogger(DealValidator.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Validates a DealDto
     * 
     * @param dealDto the deal to validate
     * @return ValidationResult containing validation status and error message if invalid
     */
    public ValidationResult validate(DealDto dealDto) {
        logger.debug("Validating deal: {}", dealDto != null ? dealDto.getDealId() : "null");
        
        if (dealDto == null) {
            logger.warn("Validation failed: Deal data is null");
            return new ValidationResult(false, "Deal data is null");
        }

        // Validate Deal ID
        if (StringUtils.isBlank(dealDto.getDealId())) {
            logger.warn("Validation failed: Deal ID is missing or empty");
            return new ValidationResult(false, "Deal ID is missing or empty");
        }
        logger.debug("Deal ID validation passed: {}", dealDto.getDealId());

        // Validate From Currency
        if (StringUtils.isBlank(dealDto.getFromCurrency())) {
            logger.warn("Validation failed: From Currency is missing or empty for deal {}", dealDto.getDealId());
            return new ValidationResult(false, "From Currency is missing or empty");
        }
        if (!CurrencyCode.isValid(dealDto.getFromCurrency())) {
            logger.warn("Validation failed: Invalid From Currency code '{}' for deal {}", 
                dealDto.getFromCurrency(), dealDto.getDealId());
            return new ValidationResult(false, "Invalid From Currency code: " + dealDto.getFromCurrency());
        }
        logger.debug("From Currency validation passed: {}", dealDto.getFromCurrency());

        // Validate To Currency
        if (StringUtils.isBlank(dealDto.getToCurrency())) {
            logger.warn("Validation failed: To Currency is missing or empty for deal {}", dealDto.getDealId());
            return new ValidationResult(false, "To Currency is missing or empty");
        }
        if (!CurrencyCode.isValid(dealDto.getToCurrency())) {
            logger.warn("Validation failed: Invalid To Currency code '{}' for deal {}", 
                dealDto.getToCurrency(), dealDto.getDealId());
            return new ValidationResult(false, "Invalid To Currency code: " + dealDto.getToCurrency());
        }
        logger.debug("To Currency validation passed: {}", dealDto.getToCurrency());

        // Validate DateTime
        if (StringUtils.isBlank(dealDto.getDateTime())) {
            logger.warn("Validation failed: Deal timestamp is missing or empty for deal {}", dealDto.getDealId());
            return new ValidationResult(false, "Deal timestamp is missing or empty");
        }
        try {
            LocalDateTime.parse(dealDto.getDateTime(), DATE_TIME_FORMATTER);
            logger.debug("DateTime validation passed: {}", dealDto.getDateTime());
        } catch (DateTimeParseException e) {
            logger.warn("Validation failed: Invalid date format '{}' for deal {}. Expected: yyyy-MM-dd HH:mm:ss", 
                dealDto.getDateTime(), dealDto.getDealId());
            return new ValidationResult(false, 
                "Invalid date format. Expected: yyyy-MM-dd HH:mm:ss, got: " + dealDto.getDateTime());
        }

        // Validate Amount
        if (StringUtils.isBlank(dealDto.getAmount())) {
            logger.warn("Validation failed: Deal amount is missing or empty for deal {}", dealDto.getDealId());
            return new ValidationResult(false, "Deal amount is missing or empty");
        }
        try {
            BigDecimal amount = new BigDecimal(dealDto.getAmount());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Validation failed: Deal amount must be greater than zero for deal {}. Got: {}", 
                    dealDto.getDealId(), dealDto.getAmount());
                return new ValidationResult(false, "Deal amount must be greater than zero");
            }
            logger.debug("Amount validation passed: {}", dealDto.getAmount());
        } catch (NumberFormatException e) {
            logger.warn("Validation failed: Invalid amount format '{}' for deal {}", 
                dealDto.getAmount(), dealDto.getDealId());
            return new ValidationResult(false, "Invalid amount format: " + dealDto.getAmount());
        }

        logger.info("Deal validation successful for deal ID: {}", dealDto.getDealId());
        return new ValidationResult(true, null);
    }

    /**
     * Result of validation operation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

