package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Integer attendanceId; // 출근 기록 ID
    private Integer contractId; // 계약 ID (연관 관계)
    private LocalTime actualStart; // 실제 근무 시작 시간
    private LocalTime actualEnd; // 실제 근무 종료 시간
    private LocalTime recognizedworkHour; // 인정된 근무 시간
    private LocalTime overtimeHours; // 초과 근무 시간
    private String commuteStatus; // 출근 상태 (정상, 지각 등)
}
