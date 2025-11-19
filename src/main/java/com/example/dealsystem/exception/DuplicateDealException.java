package com.example.dealsystem.exception;

/**
 * Exception thrown when attempting to import a duplicate deal
 */
public class DuplicateDealException extends RuntimeException {
    
    public DuplicateDealException(String message) {
        super(message);
    }
}

