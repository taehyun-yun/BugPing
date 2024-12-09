package com.example.FinalProject.entity.attendance;

import com.example.FinalProject.entity.employment.Contract;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

//@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attendanceId;
    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;
    private LocalTime actualStart;
    private LocalTime actualEnd;
    private String commuteStatus;
    @Column(length = 5000)
    private String remark; //특이사항
    private String isNormalAttendace;
    private LocalTime recognizedworkHour;
    private String overtimeStatus;
    private LocalTime overtimeHours;
    private LocalTime totaltime;
}
