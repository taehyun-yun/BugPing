package com.example.FinalProject.entity.employment;

import com.example.FinalProject.entity.employment.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "WorkChange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WorkChangeId", nullable = false, updatable = false)
    private Integer WorkChangeId; // Primary Key

    @Column(name = "scheduleId", nullable = false, updatable = false)
    private Integer scheduleId;

    @Column(name = "contractId", nullable = false)
    private Integer contractId;

    @Column(name="changedate", nullable = false)
    private LocalDate changedate;

    @Column(name = "changeType", nullable = false)
    private Boolean changeType; // true = 근무 있음, false = 근무 없음

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleId", insertable = false, updatable = false)
    private Schedule schedule;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "contractId", insertable = false, updatable = false)
//    private Contract contract; // Contract와의 관계를 명시적으로 설정할 경우 사용
}
