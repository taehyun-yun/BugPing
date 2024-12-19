package com.example.FinalProject.entity.employment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WorkChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workChangeId;

    @JoinColumn(name = "schedule_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    private LocalDate changeDate;
    private LocalDateTime changeStartTime;
    private LocalDateTime changeEndTime;
    private String inOut;
    private boolean permit;
    private String status; // 비-활성화
}