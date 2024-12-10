package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Integer employeeId; // 직원 ID
    private String name; // 이름
    private String startDate; // 정산 시작일
    private String hourlyWage; // 시급
    private Integer monthlyHours; // 월 근무시간
    private Integer workDays; // 월 근무일수
    private Double totalSalary; // 총 급여
    private Boolean isPaid; // 지급 여부
}
