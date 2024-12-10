package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Integer attendanceId; // 출근 기록 ID
    private Integer contractId; // 계약 ID (연관 관계를 ID로 표현)
    private LocalDateTime actualStart; // 실제 근무 시작 시간
    private LocalDateTime actualEnd; // 실제 근무 종료 시간
    private String commuteStatus; // 근무상태 (정상, 지각, 조퇴 등)
    private String remark; // 특이사항
    private String isNormalAttendance; // 정상 출근 여부
    private int recognizedWorkMinute; // 인정 근무 시간 (단위 : 분)
    private String overTimeStatus; // 추가 근무 상태
    private int overtimeMinute; // 추가 근무 시간 (단위 : 분)
    private int totalMinute; // 총 근무 시간 (단위 : 분)
}
