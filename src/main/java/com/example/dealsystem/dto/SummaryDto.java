package com.example.dealsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for transaction summary information
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDto {
    private String fileName;
    private String status;
    private Long validCount;
    private Long invalidCount;
    private Long processingDurationMs;
    private String startedAt;
    private String completedAt;
    private String errorMessage;
}

