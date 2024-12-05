package com.example.FinalProject.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractScheduleDto {

    // Contract fields
    private Integer contractId;
    private Integer workId;
    private Integer hourlyWage;
    private LocalDateTime contractStart;
    private LocalDateTime contractEnd;
    private Integer userId;

    // Schedule fields
    private Integer scheduleId;
    private Integer day; // 1 = Monday, ..., 7 = Sunday
    private LocalDateTime officialStart;
    private LocalDateTime officialEnd;
    private String breakHour;
    private String workHour;
}