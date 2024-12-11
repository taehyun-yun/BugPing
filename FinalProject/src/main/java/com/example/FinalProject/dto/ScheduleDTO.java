package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor

public class ScheduleDTO {
    private Integer contractId;         // 계약 ID만 포함
    private LocalTime officialStart;    // 근무 시작 시간
    private LocalTime officialEnd;      // 근무 종료 시간
    private String name;                // 사용자 이름
}

