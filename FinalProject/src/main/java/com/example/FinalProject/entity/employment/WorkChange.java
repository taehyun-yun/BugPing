package com.example.FinalProject.entity.employment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WorkChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer changeId;
    @JoinColumn(name = "schedule_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;
    private LocalDate changeDate;
    private LocalTime changeStartTime;
    private LocalTime changeEndTime;
    private String inOut;
}