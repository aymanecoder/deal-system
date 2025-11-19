package com.example.dealsystem.repository;

import com.example.dealsystem.domain.InvalidDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidDealRepository extends JpaRepository<InvalidDeal, Long> {
    
    @Query("SELECT COUNT(i) FROM InvalidDeal i WHERE i.fileName = :fileName")
    Long countByFileName(String fileName);
}

