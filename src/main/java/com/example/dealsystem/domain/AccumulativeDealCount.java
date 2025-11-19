package com.example.dealsystem.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accumulative_deal_count", indexes = {
    @Index(name = "idx_currency_code", columnList = "currency_code", unique = true)
})
@Getter
@Setter
public class AccumulativeDealCount extends AbstractDomain {

    @Column(name = "currency_code", nullable = false, unique = true, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    @Column(name = "count_of_deals", nullable = false)
    private Long countOfDeals;

    public AccumulativeDealCount() {
        this.countOfDeals = 0L;
    }

    public AccumulativeDealCount(CurrencyCode currencyCode) {
        this();
        this.currencyCode = currencyCode;
    }

    public void increment(long count) {
        this.countOfDeals += count;
    }
}

