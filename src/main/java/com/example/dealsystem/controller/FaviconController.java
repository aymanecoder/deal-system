package com.example.dealsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle favicon requests and prevent 404 errors
 */
@RestController
public class FaviconController {

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

