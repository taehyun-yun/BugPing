package com.example.FinalProject.entity.employment;

import com.example.FinalProject.entity.work.Work;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contractId;

    @JoinColumn(name = "work_id")
    @ManyToOne
    private Work work;

    private int hourlyWage; // 시급
    private LocalDate contractStart; // 적용 시작일
    private LocalDate contractEnd; // 적용 종료일
}
