package com.example.FinalProject.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminAttendanceDTO {
    private String userId;
    private String userName;
    private Integer attendanceId;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String commuteStatus;
    private String remark;
    private String isNormalAttendance;
    private int totalMinute;

    // 스케줄 정보 추가
    private LocalTime officialStart;
    private LocalTime officialEnd;

    // 스케줄만 사용하는 생성자
    public AdminAttendanceDTO(String userId, String userName, LocalTime officialStart, LocalTime officialEnd) {
        this.userId = userId;
        this.userName = userName;
        this.officialStart = officialStart;
        this.officialEnd = officialEnd;
        this.commuteStatus = "미출근"; // 기본값
        this.isNormalAttendance = "N"; // 기본값
        this.totalMinute = 0; // 기본값
    }
}
