package com.example.FinalProject.entity.attendance;

import com.example.FinalProject.entity.contract.Contract;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private Integer attendanceId;
    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String commuteStatus;
    @Column(length = 5000)
    private String remark; //특이사항
    private String isNormalAttendace;
    private int recognizedworkMinute;
    private String overtimeStatus;
    private int overtimeMinute;
    private int totalMinute;
}
