package com.example.dealsystem.validation;

import com.example.dealsystem.dto.DealDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DealValidatorTest {

    private DealValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DealValidator();
    }

    @Test
    void testValidate_ValidDeal() {
        DealDto dealDto = new DealDto();
        dealDto.setDealId("DEAL001");
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("EUR");
        dealDto.setDateTime("2024-01-15 10:30:00");
        dealDto.setAmount("1000.50");

        DealValidator.ValidationResult result = validator.validate(dealDto);

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidate_NullDeal() {
        DealValidator.ValidationResult result = validator.validate(null);
        assertFalse(result.isValid());
        assertEquals("Deal data is null", result.getErrorMessage());
    }

    @Test
    void testValidate_MissingDealId() {
        DealDto dealDto = new DealDto();
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("EUR");
        dealDto.setDateTime("2024-01-15 10:30:00");
        dealDto.setAmount("1000.50");

        DealValidator.ValidationResult result = validator.validate(dealDto);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Deal ID"));
    }

    @Test
    void testValidate_InvalidFromCurrency() {
        DealDto dealDto = new DealDto();
        dealDto.setDealId("DEAL001");
        dealDto.setFromCurrency("XXX");
        dealDto.setToCurrency("EUR");
        dealDto.setDateTime("2024-01-15 10:30:00");
        dealDto.setAmount("1000.50");

        DealValidator.ValidationResult result = validator.validate(dealDto);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid From Currency"));
    }

    @Test
    void testValidate_InvalidToCurrency() {
        DealDto dealDto = new DealDto();
        dealDto.setDealId("DEAL001");
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("YYY");
        dealDto.setDateTime("2024-01-15 10:30:00");
        dealDto.setAmount("1000.50");

        DealValidator.ValidationResult result = validator.validate(dealDto);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid To Currency"));
    }

    @Test
    void testValidate_InvalidDateTime() {
        DealDto dealDto = new DealDto();
        dealDto.setDealId("DEAL001");
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("EUR");
        dealDto.setDateTime("2024-01-15");
        dealDto.setAmount("1000.50");

        DealValidator.ValidationResult result = validator.validate(dealDto);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid date format"));
    }

    @Test
    void testValidate_InvalidAmount() {
        DealDto dealDto = new DealDto();
        dealDto.setDealId("DEAL001");
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("EUR");
        dealDto.setDateTime("2024-01-15 10:30:00");
        dealDto.setAmount("invalid");

        DealValidator.ValidationResult result = validator.validate(dealDto);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Invalid amount format"));
    }

    @Test
    void testValidate_ZeroAmount() {
        DealDto dealDto = new DealDto();
        dealDto.setDealId("DEAL001");
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("EUR");
        dealDto.setDateTime("2024-01-15 10:30:00");
        dealDto.setAmount("0");

        DealValidator.ValidationResult result = validator.validate(dealDto);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("greater than zero"));
    }

    @Test
    void testValidate_NegativeAmount() {
        DealDto dealDto = new DealDto();
        dealDto.setDealId("DEAL001");
        dealDto.setFromCurrency("USD");
        dealDto.setToCurrency("EUR");
        dealDto.setDateTime("2024-01-15 10:30:00");
        dealDto.setAmount("-100");

        DealValidator.ValidationResult result = validator.validate(dealDto);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("greater than zero"));
    }
}

