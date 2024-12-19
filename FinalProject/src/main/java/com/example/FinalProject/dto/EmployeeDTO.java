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
public class EmployeeDTO {
    private String employeeId; // 직원 ID
    private String name;        // 이름
    private String startDate;   // 정산 시작일 (String 타입)
    private String hireDate; // 고용 시작일 필드
    private String hourlyWage;  // 시급
    private double monthlyHours; // 월 근무시간
    private Integer workDays;   // 월 근무일수
    private double totalSalary; // 총 급여
    private double basicSalary; // 기본급
    private Boolean isPaid;     // 지급 여부
    private Integer payRollId; // 추가
    private double weeklyAllowance; // 주휴수당
    private double nightPay;       // 야간수당
    private double overtimePay;     // 초과 근무 수당
    private double deduction;       // 공제액
}