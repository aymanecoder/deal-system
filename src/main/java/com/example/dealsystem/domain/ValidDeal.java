package com.example.dealsystem.domain;

import com.example.dealsystem.dto.DealDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "valid_deal", indexes = {
    @Index(name = "idx_deal_id", columnList = "deal_id", unique = true),
    @Index(name = "idx_from_currency", columnList = "from_currency"),
    @Index(name = "idx_file_name", columnList = "file_name")
})
@Getter
@Setter
public class ValidDeal extends Deal {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Column(name = "deal_id", nullable = false, unique = true, length = 100)
    private String dealId;

    @Column(name = "from_currency", nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyCode fromCurrency;

    @Column(name = "to_currency", nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyCode toCurrency;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    public ValidDeal() {
    }

    public static ValidDeal valueOf(DealDto dealDto) {
        ValidDeal validDeal = new ValidDeal();
        validDeal.setDealId(dealDto.getDealId());
        validDeal.setFromCurrency(CurrencyCode.valueOf(dealDto.getFromCurrency().toUpperCase()));
        validDeal.setToCurrency(CurrencyCode.valueOf(dealDto.getToCurrency().toUpperCase()));
        validDeal.setAmount(new BigDecimal(dealDto.getAmount()));
        validDeal.setDateTime(LocalDateTime.parse(dealDto.getDateTime(), FORMATTER));
        return validDeal;
    }
}

