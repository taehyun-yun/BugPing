package com.example.FinalProject.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyAttendanceDTO {
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
