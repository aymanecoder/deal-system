package com.example.dealsystem.service.deals;

import com.example.dealsystem.domain.AccumulativeDealCount;
import com.example.dealsystem.domain.CurrencyCode;
import com.example.dealsystem.domain.InvalidDeal;
import com.example.dealsystem.domain.ValidDeal;
import com.example.dealsystem.repository.AccumulativeDealCountRepository;
import com.example.dealsystem.repository.InvalidDealRepository;
import com.example.dealsystem.repository.ValidDealRepository;
import com.example.dealsystem.dto.DealDto;
import com.example.dealsystem.validation.DealValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private ValidDealRepository validDealRepository;

    @Mock
    private InvalidDealRepository invalidDealRepository;

    @Mock
    private AccumulativeDealCountRepository accumulativeDealCountRepository;

    @Mock
    private DealValidator dealValidator;

    @InjectMocks
    private DealService dealService;

    private DealDto validDealDto;
    private String fileName;

    @BeforeEach
    void setUp() {
        fileName = "test_deals.csv";
        validDealDto = new DealDto();
        validDealDto.setDealId("DEAL001");
        validDealDto.setFromCurrency("USD");
        validDealDto.setToCurrency("EUR");
        validDealDto.setDateTime("2024-01-15 10:30:00");
        validDealDto.setAmount("1000.50");
    }

    @Test
    void testProcessDeal_ValidDeal() {
        when(validDealRepository.existsByDealId(anyString())).thenReturn(false);
        when(dealValidator.validate(any(DealDto.class)))
            .thenReturn(new DealValidator.ValidationResult(true, null));
        when(validDealRepository.save(any(ValidDeal.class))).thenReturn(new ValidDeal());

        dealService.processDeal(validDealDto, fileName, "row data");

        verify(validDealRepository, times(1)).existsByDealId(validDealDto.getDealId());
        verify(dealValidator, times(1)).validate(validDealDto);
        verify(validDealRepository, times(1)).save(any(ValidDeal.class));
        verify(invalidDealRepository, never()).save(any(InvalidDeal.class));
    }

    @Test
    void testProcessDeal_DuplicateDealId() {
        when(validDealRepository.existsByDealId(anyString())).thenReturn(true);
        when(invalidDealRepository.save(any(InvalidDeal.class))).thenReturn(new InvalidDeal());

        dealService.processDeal(validDealDto, fileName, "row data");

        verify(validDealRepository, times(1)).existsByDealId(validDealDto.getDealId());
        verify(dealValidator, never()).validate(any(DealDto.class));
        verify(invalidDealRepository, times(1)).save(any(InvalidDeal.class));
        verify(validDealRepository, never()).save(any(ValidDeal.class));
    }

    @Test
    void testProcessDeal_InvalidDeal() {
        when(validDealRepository.existsByDealId(anyString())).thenReturn(false);
        when(dealValidator.validate(any(DealDto.class)))
            .thenReturn(new DealValidator.ValidationResult(false, "Invalid currency"));
        when(invalidDealRepository.save(any(InvalidDeal.class))).thenReturn(new InvalidDeal());

        dealService.processDeal(validDealDto, fileName, "row data");

        verify(validDealRepository, times(1)).existsByDealId(validDealDto.getDealId());
        verify(dealValidator, times(1)).validate(validDealDto);
        verify(invalidDealRepository, times(1)).save(any(InvalidDeal.class));
        verify(validDealRepository, never()).save(any(ValidDeal.class));
    }

    @Test
    void testUpdateAccumulativeCounts() {
        ValidDeal deal1 = new ValidDeal();
        deal1.setFileName(fileName);
        deal1.setFromCurrency(CurrencyCode.USD);

        ValidDeal deal2 = new ValidDeal();
        deal2.setFileName(fileName);
        deal2.setFromCurrency(CurrencyCode.USD);

        ValidDeal deal3 = new ValidDeal();
        deal3.setFileName(fileName);
        deal3.setFromCurrency(CurrencyCode.EUR);

        List<ValidDeal> allDeals = List.of(deal1, deal2, deal3);

        // Mock repository correctly
        when(validDealRepository.findByFileName(fileName)).thenReturn(allDeals);
        when(accumulativeDealCountRepository.findByCurrencyCode(CurrencyCode.USD))
                .thenReturn(Optional.empty());
        when(accumulativeDealCountRepository.findByCurrencyCode(CurrencyCode.EUR))
                .thenReturn(Optional.empty());

        dealService.updateAccumulativeCounts(fileName);

        verify(accumulativeDealCountRepository, times(2)).save(any(AccumulativeDealCount.class));
    }

}

