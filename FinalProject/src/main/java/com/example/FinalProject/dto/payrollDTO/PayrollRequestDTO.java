package com.example.FinalProject.dto.payrollDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRequestDTO { // 급여 계산시 요청 DTO(급여 계산 시 필요한 조건(기간, 직원 ID 등)을 전달받기 위한 DTO)
    private String userId;    // 직원 ID
    private LocalDateTime startDate;   // 급여 계산 시작일
    private LocalDateTime endDate;     // 급여 계산 종료일
}
