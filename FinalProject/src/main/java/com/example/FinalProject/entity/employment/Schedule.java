package com.example.FinalProject.entity.employment;

import com.example.FinalProject.entity.employment.Contract;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer scheduleId;


    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")//, nullable = false, insertable = false, updatable = false
    private Contract contract;

    private Integer day; // Monday == 1 , Sunday == 7
    private LocalTime officialStart; //LocalTime.of(12,30) 12시 30분
    private LocalTime officialEnd;
    private Integer breakMinute; // 1 = 1분
    private String status; // 비-활성화
}
