package com.example.FinalProject.entity.employment;

import com.example.FinalProject.entity.work.Work;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "contractId", nullable = false, updatable = false)
    private Integer contractId;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")//, nullable = false, insertable = false, updatable = false
    private Work work;

    private int hourlyWage;
    private LocalDateTime contractStart; //LocalDate -> LocalDateTime 변경 (캘린더 사용 시 변환 필요)
    private LocalDateTime contractEnd;
    private String status; // 계약 비-활성화
}
