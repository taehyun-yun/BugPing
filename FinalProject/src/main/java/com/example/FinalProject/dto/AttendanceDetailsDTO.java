package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttendanceDetailsDTO {
    private long totalScheduled;
    private long attended;
    private long onLeave;
    private long notYetStarted;
    private long tardy; // 지각
    private long earlyLeave; // 조퇴
    private double attendanceRate; // 출근율 추가
}
