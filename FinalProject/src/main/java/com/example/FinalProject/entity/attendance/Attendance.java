package com.example.FinalProject.entity.attendance;

import com.example.FinalProject.entity.contract.Contract;
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
    private Integer attendanceId;
    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;
    private LocalTime actualStart; // 출근 시간
    private LocalTime actualEnd; // 퇴근 시간
    private String commuteStatus;  // 근무 상태
    @Column(length = 5000)
    private String remark; //특이사항
    private String isNormalAttendace; // 정상 출근 여부
    private LocalTime recognizedworkHour; // 인정 근무 시간
    private String overtimeStatus; // 추가 근무 상태
    private LocalTime overtimeHours; // 추가 근무 시간
    private LocalTime totaltime; // 총 근무 시간
}
