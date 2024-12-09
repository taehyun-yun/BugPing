package com.example.FinalProject.entity.attendance;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.entity.work.Work;
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

    //1안
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")//, nullable = false, insertable = false, updatable = false
    private Schedule schedule;

    //1안
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id")//, nullable = false, insertable = false, updatable = false
    private WorkChange workChange;

//    //2안
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "work_id")//, nullable = false, insertable = false, updatable = false
//    private Work work;

    private LocalTime actualStart;
    private LocalTime actualEnd;
    private String commuteStatus;

    @Column(length = 5000)
    private String remark; //특이사항

    private String isNormalAttendance;//오타수정
    private int recognizedWorkMinute;//오타수정, int로 변경
    private String overtimeStatus;
    private int overtimeMinute;//int로 변경
    private int totalMinute;//오타수정, int로 변경
}
