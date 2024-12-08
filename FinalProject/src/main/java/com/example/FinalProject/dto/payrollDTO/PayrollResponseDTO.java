package com.example.FinalProject.dto.payrollDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponseDTO { // 급여 계산 결과 반환하는 DTO (기본 급여, 초과 근무 수당) 등을 포함 
    private Integer employeeId;        // 직원 ID
    private String employeeName;       // 직원 이름
    private double totalBasicSalary;   // 기본 급여 (정상 근무 시간 × 시급)
    private double totalOvertimePay;   // 초과 근무 수당
    private double totalSalary;        // 총 급여
}
