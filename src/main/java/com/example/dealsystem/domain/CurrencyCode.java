package com.example.dealsystem.domain;

import lombok.Getter;

@Getter
public enum CurrencyCode {
    USD("US Dollar"),
    EUR("Euro"),
    GBP("British Pound"),
    JPY("Japanese Yen"),
    AUD("Australian Dollar"),
    CAD("Canadian Dollar"),
    CHF("Swiss Franc"),
    CNY("Chinese Yuan"),
    NZD("New Zealand Dollar"),
    SEK("Swedish Krona"),
    NOK("Norwegian Krone"),
    DKK("Danish Krone"),
    SGD("Singapore Dollar"),
    HKD("Hong Kong Dollar"),
    INR("Indian Rupee"),
    KRW("South Korean Won"),
    MXN("Mexican Peso"),
    BRL("Brazilian Real"),
    ZAR("South African Rand"),
    RUB("Russian Ruble");

    private final String description;

    CurrencyCode(String description) {
        this.description = description;
    }

    public static boolean isValid(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        try {
            CurrencyCode.valueOf(code.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

