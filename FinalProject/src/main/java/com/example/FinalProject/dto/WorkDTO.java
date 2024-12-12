package com.example.FinalProject.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkDTO {
    private Integer workId;
    private String userId;
    private String userName;
    private LocalDate hireDate;
    private LocalDate resignDate;
}
