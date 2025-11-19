package com.example.dealsystem.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "invalid_deal", indexes = {
    @Index(name = "idx_invalid_file_name", columnList = "file_name")
})
@Getter
@Setter
public class InvalidDeal extends Deal {

    @Column(name = "deal_id", length = 100)
    private String dealId;

    @Column(name = "from_currency", length = 3)
    private String fromCurrency;

    @Column(name = "to_currency", length = 3)
    private String toCurrency;

    @Column(name = "date_time", length = 50)
    private String dateTime;

    @Column(name = "amount", length = 50)
    private String amount;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "row_data", columnDefinition = "TEXT")
    private String rowData;

    public InvalidDeal() {
    }
}

