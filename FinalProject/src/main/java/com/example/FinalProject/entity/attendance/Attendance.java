package com.example.FinalProject.entity.attendance;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    //1안
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")//, nullable = false, insertable = false, updatable = false
    private Schedule schedule;

    //1안
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id")//, nullable = false, insertable = false, updatable = false
    private WorkChange workChange;

//    2안
//    @ManyToOne
//    @JoinColumn(name = "contract_id")
//    private Contract contract;

    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String commuteStatus;
    @Column(length = 5000)
    private String remark; //특이사항
    private String isNormalAttendance;
    private int recognizedWorkMinute;
    private String overtimeStatus;
    private int overtimeMinute;
    private int totalMinute;
}
