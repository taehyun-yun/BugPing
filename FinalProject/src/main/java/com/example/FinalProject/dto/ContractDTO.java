package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {
    private Integer contractId;     // 계약 ID
    private String companyName;     // 회사 이름
    private Integer hourlyWage;     // 시급
    private LocalDate contractStart; // 계약 시작일
    private LocalDate contractEnd;   // 계약 종료일
}
