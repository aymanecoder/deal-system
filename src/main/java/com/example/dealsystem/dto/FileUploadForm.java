package com.example.dealsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for file upload form
 */
@Getter
@Setter
public class FileUploadForm {

    @NotNull(message = "Please select a CSV file")
    private MultipartFile file;
}

