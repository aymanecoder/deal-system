package com.example.dealsystem.repository;

import com.example.dealsystem.domain.AccumulativeDealCount;
import com.example.dealsystem.domain.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccumulativeDealCountRepository extends JpaRepository<AccumulativeDealCount, Long> {
    
    Optional<AccumulativeDealCount> findByCurrencyCode(CurrencyCode currencyCode);
    
    @Modifying
    @Query("UPDATE AccumulativeDealCount a SET a.countOfDeals = a.countOfDeals + :count WHERE a.currencyCode = :currencyCode")
    void incrementCountByCurrencyCode(CurrencyCode currencyCode, Long count);
}

