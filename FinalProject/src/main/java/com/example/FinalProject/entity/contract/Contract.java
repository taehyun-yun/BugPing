package com.example.FinalProject.entity.contract;

import com.example.FinalProject.entity.company.Company;
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
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;
    private int hourlyWage; // 시급
    private LocalDate contractStart; // 적용 시작일
    private LocalDate contractEnd; // 적용 종료일
}
