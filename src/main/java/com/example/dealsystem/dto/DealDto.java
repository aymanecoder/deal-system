package com.example.dealsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Deal information from CSV file
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DealDto {
    private String dealId;
    private String fromCurrency;
    private String toCurrency;
    private String dateTime;
    private String amount;
}

