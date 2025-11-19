package com.example.dealsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyCodeTest {

    @Test
    void testIsValid_ValidCurrency() {
        assertTrue(CurrencyCode.isValid("USD"));
        assertTrue(CurrencyCode.isValid("EUR"));
        assertTrue(CurrencyCode.isValid("GBP"));
        assertTrue(CurrencyCode.isValid("usd")); // Case insensitive
        assertTrue(CurrencyCode.isValid("EUR"));
    }

    @Test
    void testIsValid_InvalidCurrency() {
        assertFalse(CurrencyCode.isValid("XXX"));
        assertFalse(CurrencyCode.isValid("ABC"));
        assertFalse(CurrencyCode.isValid("123"));
    }

    @Test
    void testIsValid_NullOrEmpty() {
        assertFalse(CurrencyCode.isValid(null));
        assertFalse(CurrencyCode.isValid(""));
        assertFalse(CurrencyCode.isValid("   "));
    }

    @Test
    void testValueOf() {
        assertEquals(CurrencyCode.USD, CurrencyCode.valueOf("USD"));
        assertEquals(CurrencyCode.EUR, CurrencyCode.valueOf("EUR"));
        assertEquals(CurrencyCode.GBP, CurrencyCode.valueOf("GBP"));
    }
}

