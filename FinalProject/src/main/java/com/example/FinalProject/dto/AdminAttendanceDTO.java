package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
