package com.example.dealsystem.controller;

import com.example.dealsystem.domain.TransactionLog;
import com.example.dealsystem.dto.FileUploadForm;
import com.example.dealsystem.exception.DuplicateFileException;
import com.example.dealsystem.service.csv.FileUploadService;
import com.example.dealsystem.service.logging.TransactionLogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for handling file upload requests
 */
@Controller
@RequestMapping("/upload")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final TransactionLogService transactionLogService;
    private final FileUploadService fileUploadService;

    public FileUploadController(TransactionLogService transactionLogService,
                                FileUploadService fileUploadService) {
        this.transactionLogService = transactionLogService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping
    public ModelAndView getUploadPage() {
        logger.debug("Accessing upload page");
        return getModelView();
    }

    @PostMapping
    public ModelAndView uploadFile(@ModelAttribute("fileUploadForm") @Valid FileUploadForm form,
                                    BindingResult bindingResult,
                                    Model model) {
        logger.info("Received file upload request");
        
        if (bindingResult.hasErrors()) {
            logger.warn("File upload form validation failed: {}", bindingResult.getAllErrors());
            return new ModelAndView("views/upload-file");
        }

        String fileName = form.getFile().getOriginalFilename();
        logger.info("Processing file upload: {}", fileName);
        
        // Check if file was already imported
        if (transactionLogService.isFileAlreadyImported(fileName)) {
            logger.warn("Attempted to import duplicate file: {}", fileName);
            throw new DuplicateFileException("File '" + fileName + "' has already been imported.");
        }

        TransactionLog transactionLog = transactionLogService.save(new TransactionLog(fileName));
        MDC.put("logId", transactionLog.getId().toString());
        logger.info("Created transaction log entry with ID: {} for file: {}", 
            transactionLog.getId(), fileName);

        try {
            fileUploadService.uploadFile(form.getFile(), transactionLog);
            
            logger.info("File upload successful: {}", fileName);
            ModelAndView modelAndView = getModelView();
            modelAndView.addObject("success", true);
            modelAndView.addObject("fileName", fileName);
            return modelAndView;
        } catch (Exception e) {
            logger.error("Failed importing file: {}", fileName, e);
            ModelAndView modelAndView = getModelView();
            modelAndView.addObject("error", true);
            modelAndView.addObject("errorMessage", "Failed to import file: " + e.getMessage());
            return modelAndView;
        } finally {
            MDC.clear();
        }
    }

    private ModelAndView getModelView() {
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new FileUploadForm());
        return modelAndView;
    }
}

