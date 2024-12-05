package com.example.FinalProject.entity.commute;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendanceId", nullable = false, updatable = false)
    private Integer attendanceId;

    @Column(name = "contractId", nullable = false)
    private Integer contractId;

    @Column(name = "actualStart")
    private LocalDateTime actualStart;

    @Column(name = "actualEnd")
    private LocalDateTime actualEnd;

    @Column(name = "commuteStatus")
    private String commuteStatus;

    @Column(name = "remark")
    private String remark;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "isNormalAttendance")
    private String isNormalAttendance;

    @Column(name = "recognizedWorkHours")
    private LocalDateTime recognizedWorkHours;

    @Column(name = "overtimeStatus")
    private String overtimeStatus;

    @Column(name = "overtimeHours")
    private LocalDateTime overtimeHours;

    @Column(name = "totalHours")
    private LocalDateTime totalHours;

//    // Relationships
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "contractId", insertable = false, updatable = false)
//    private Contract contract;
}
