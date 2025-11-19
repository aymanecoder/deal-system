package com.example.dealsystem.repository;

import com.example.dealsystem.domain.CurrencyCode;
import com.example.dealsystem.domain.ValidDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValidDealRepository extends JpaRepository<ValidDeal, Long> {
    
    Optional<ValidDeal> findByDealId(String dealId);
    
    boolean existsByDealId(String dealId);
    
    @Query("SELECT COUNT(v) FROM ValidDeal v WHERE v.fileName = :fileName")
    Long countByFileName(String fileName);
    
    @Query("SELECT v FROM ValidDeal v WHERE v.fileName = :fileName")
    List<ValidDeal> findByFileName(@Param("fileName") String fileName);
    
    @Query("SELECT COUNT(v) FROM ValidDeal v WHERE v.fileName = :fileName AND v.fromCurrency = :currency")
    Long countByFileNameAndCurrency(@Param("fileName") String fileName, @Param("currency") CurrencyCode currency);
}

