package com.example.FinalProject.dto.payrollDTO;

import com.example.FinalProject.dto.AttendanceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponseDTO { // 급여 계산 결과 반환하는 DTO (기본 급여, 초과 근무 수당) 등을 포함
    private String userId;        // 직원 ID
    private String name;       // 직원 이름 (유저 테이블의 name)
    private double basicSalary;   // 기본 급여 (정상 근무 시간 × 시급)
    private double weeklyAllowance;    // 주휴 수당
    private double overtimePay;   // 초과 근무 수당
    private double nightPay;      // 야간 수당
    private double deduction;     // 공제액
    private double totalSalary;   // 총 급여(실 수령액)
    private double hourlyWage; // 시급
    private LocalDateTime startDate;
}
