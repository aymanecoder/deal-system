package com.example.dealsystem.exception;

/**
 * Exception thrown when file processing fails
 */
public class FileProcessingException extends RuntimeException {
    
    public FileProcessingException(String message) {
        super(message);
    }
    
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

