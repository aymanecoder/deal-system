package com.example.dealsystem.exception;

/**
 * Exception thrown when attempting to import a file that was already imported
 */
public class DuplicateFileException extends RuntimeException {
    
    public DuplicateFileException(String message) {
        super(message);
    }
}

