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
<<<<<<< HEAD

    @ManyToOne
    @JoinColumn(name = "schedule_id")
=======
    //1안
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")//, nullable = false, insertable = false, updatable = false
>>>>>>> 564d4d18815f3c378fed769b10d81ebfe74b855b
    private Schedule schedule;

    //1안
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id")//, nullable = false, insertable = false, updatable = false
    private WorkChange workChange;

<<<<<<< HEAD
    @Column(name = "actual_start")
=======
//    2안
//    @ManyToOne
//    @JoinColumn(name = "contract_id")
//    private Contract contract;

>>>>>>> 564d4d18815f3c378fed769b10d81ebfe74b855b
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String commuteStatus;
    @Column(length = 5000)
<<<<<<< HEAD
    private String remark;

=======
    private String remark; //특이사항
>>>>>>> 564d4d18815f3c378fed769b10d81ebfe74b855b
    private String isNormalAttendance;
    private int recognizedWorkMinute;
    private String overtimeStatus;
    private int overtimeMinute;
    private int totalMinute;
<<<<<<< HEAD
}
=======
}
>>>>>>> 564d4d18815f3c378fed769b10d81ebfe74b855b
