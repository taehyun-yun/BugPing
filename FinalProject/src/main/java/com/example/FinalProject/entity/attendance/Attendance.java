package com.example.FinalProject.entity.attendance;

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
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "attendanceId", nullable = false, updatable = false)
    private Integer attendanceId;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")//, nullable = false, insertable = false, updatable = false
    private Contract contract;

    private LocalTime actualStart;
    private LocalTime actualEnd;
    private String commuteStatus;

    @Column(length = 5000)
    private String remark; //특이사항

    private String isNormalAttendance;//오타수정
    private LocalTime recognizedWorkHours;//오타수정
    private String overtimeStatus;
    private LocalTime overtimeHours;
    private LocalTime totalTime;//오타수정
}
