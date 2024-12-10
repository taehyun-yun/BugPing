package com.example.FinalProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleChangeDTO {
    private Integer scheduleId;
    private LocalTime officialStart;
    private LocalTime officialEnd;
    private Integer breakMinute;
    private String userName; // 사용자 이름 필드

}
