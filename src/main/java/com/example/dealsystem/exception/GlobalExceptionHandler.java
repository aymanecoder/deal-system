package com.example.dealsystem.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the application
 * Handles all exceptions and provides user-friendly error messages
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateFileException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleDuplicateFileException(DuplicateFileException ex) {
        logger.warn("Duplicate file exception: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new com.example.dealsystem.dto.FileUploadForm());
        modelAndView.addObject("error", true);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(FileProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleFileProcessingException(FileProcessingException ex) {
        logger.error("File processing exception: {}", ex.getMessage(), ex);
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new com.example.dealsystem.dto.FileUploadForm());
        modelAndView.addObject("error", true);
        modelAndView.addObject("errorMessage", "Failed to process file: " + ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(DealValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleDealValidationException(DealValidationException ex) {
        logger.warn("Deal validation exception: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new com.example.dealsystem.dto.FileUploadForm());
        modelAndView.addObject("error", true);
        modelAndView.addObject("errorMessage", "Validation error: " + ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(DuplicateDealException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleDuplicateDealException(DuplicateDealException ex) {
        logger.warn("Duplicate deal exception: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new com.example.dealsystem.dto.FileUploadForm());
        modelAndView.addObject("error", true);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ModelAndView handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        logger.warn("File size exceeded maximum allowed size: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new com.example.dealsystem.dto.FileUploadForm());
        modelAndView.addObject("error", true);
        modelAndView.addObject("errorMessage", "File size exceeds maximum allowed size (100MB)");
        return modelAndView;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument exception: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new com.example.dealsystem.dto.FileUploadForm());
        modelAndView.addObject("error", true);
        modelAndView.addObject("errorMessage", "Invalid input: " + ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNoResourceFoundException(NoResourceFoundException ex) {
        // Silently handle favicon and other static resource 404s
        if (ex.getResourcePath() != null && ex.getResourcePath().contains("favicon")) {
            // Ignore favicon errors - they're harmless
            return;
        }
        logger.debug("Resource not found: {}", ex.getResourcePath());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex) {
        // Ignore favicon-related exceptions
        if (ex.getMessage() != null && ex.getMessage().contains("favicon")) {
            return null;
        }
        
        logger.error("Unexpected exception occurred", ex);
        ModelAndView modelAndView = new ModelAndView("views/upload-file");
        modelAndView.addObject("fileUploadForm", new com.example.dealsystem.dto.FileUploadForm());
        modelAndView.addObject("error", true);
        modelAndView.addObject("errorMessage", "An unexpected error occurred. Please try again or contact support.");
        return modelAndView;
    }
}

