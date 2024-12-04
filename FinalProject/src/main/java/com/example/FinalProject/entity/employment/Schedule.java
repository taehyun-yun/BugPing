package com.example.FinalProject.entity.employment;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduleId", nullable = false, updatable = false)
    private Integer scheduleId;

    @Column(name = "contractId", nullable = false)
    private Integer contractId;

    @Column(name = "day")
    private Integer day; // 1 = Monday, 7 = Sunday

    @Column(name = "officialStart")
    private LocalDateTime officialStart;

    @Column(name = "officialEnd")
    private LocalDateTime officialEnd;

    @Column(name = "breakHour")
    private String breakHour;

    @Column(name = "workHour")
    private String workHour;

//    // Relationships
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "contractId", insertable = false, updatable = false)
//    private Contract contract;
}