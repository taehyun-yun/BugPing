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

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")//, nullable = false, insertable = false, updatable = false
    private Schedule schedule;


    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id")//, nullable = false, insertable = false, updatable = false
    private WorkChange workChange;

    private LocalDateTime actualStart; //출근찍은시간
    private LocalDateTime actualEnd; //퇴근찍은시간
    private String commuteStatus; //근무상태(지각 조퇴 결근)
    @Column(length = 5000)
    private String remark; //특이사항 (결근 사유 뭐 이런거 ㅇㅇ..)

    private String isNormalAttendance; //정상출근여부 (지각했지만 출근인정 시 사용)
    private LocalDateTime recognizedWorkStart; //인정근무시작시간 -> 원래 끝나는 시간이 찍히고.
    private LocalDateTime recognizedWorkEnd; //인정근무종료시간 -> 원래 끝나는 시간이 찍혀야함.
    private int recognizedWorkBreakMinute; //인정휴게시간 (기준 : 분 )
    private int recognizedWorkMinute; //인정근무시간( 시작~종료 - 휴게시간 )

    private String overtimeStatus; //추가근무여부
    private LocalDateTime overtimeStart; //추가근무 시작시간
    private LocalDateTime overtimeEnd; //추가근무 종료시간
    private int overtimeBreakMinute; //추가휴게시간
    private int overtimeMinute; //추가근무시간 ( 시작~종료 - 휴게시간 )

    private int totalMinute; //총근무시간(인정근무시간 + 추가근무시간)
    private String status; // 비-활성화 (삭제 관련)
}
