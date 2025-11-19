package com.example.dealsystem.controller;

import com.example.dealsystem.domain.TransactionLog;
import com.example.dealsystem.dto.SummaryDto;
import com.example.dealsystem.service.logging.TransactionLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;

/**
 * Controller for displaying transaction summaries
 */
@Controller
@RequestMapping("/summary")
public class SummaryController {

    private static final Logger logger = LoggerFactory.getLogger(SummaryController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TransactionLogService transactionLogService;

    public SummaryController(TransactionLogService transactionLogService) {
        this.transactionLogService = transactionLogService;
    }

    @GetMapping
    public ModelAndView getSummaryPage(@RequestParam(required = false) String fileName, Model model) {
        logger.debug("Accessing summary page for file: {}", fileName);
        ModelAndView modelAndView = new ModelAndView("views/summary-page");
        
        if (fileName != null && !fileName.trim().isEmpty()) {
            logger.info("Searching for transaction log for file: {}", fileName);
            TransactionLog transactionLog = transactionLogService.findByFileName(fileName);
            
            if (transactionLog != null) {
                logger.info("Found transaction log for file: {} - Status: {}", 
                    fileName, transactionLog.getStatus());
                SummaryDto summary = mapToSummaryDto(transactionLog);
                modelAndView.addObject("summary", summary);
                modelAndView.addObject("found", true);
            } else {
                logger.warn("No transaction log found for file: {}", fileName);
                modelAndView.addObject("found", false);
                modelAndView.addObject("errorMessage", "No transaction found for file: " + fileName);
            }
        }
        
        return modelAndView;
    }

    private SummaryDto mapToSummaryDto(TransactionLog transactionLog) {
        SummaryDto summary = new SummaryDto();
        summary.setFileName(transactionLog.getFileName());
        summary.setStatus(transactionLog.getStatus().toString());
        summary.setValidCount(transactionLog.getValidCount() != null ? transactionLog.getValidCount() : 0L);
        summary.setInvalidCount(transactionLog.getInvalidCount() != null ? transactionLog.getInvalidCount() : 0L);
        summary.setProcessingDurationMs(transactionLog.getProcessingDurationMs());
        summary.setStartedAt(transactionLog.getStartedAt() != null ? 
            transactionLog.getStartedAt().format(FORMATTER) : null);
        summary.setCompletedAt(transactionLog.getCompletedAt() != null ? 
            transactionLog.getCompletedAt().format(FORMATTER) : null);
        summary.setErrorMessage(transactionLog.getErrorMessage());
        return summary;
    }
}

