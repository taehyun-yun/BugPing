package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDetailsDTO {
    private long totalScheduled;
    private long attended;
    private long onLeave;
    private long extraWork;
    private double attendanceRate; // 출근율 추가
}