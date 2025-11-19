package com.example.dealsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class Deal extends AbstractDomain {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    public Deal() {
    }
}

