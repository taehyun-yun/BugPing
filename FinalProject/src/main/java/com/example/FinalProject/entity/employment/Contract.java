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
    //@Column(name = "contractId", nullable = false, updatable = false)
    private Integer contractId;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")//, nullable = false, insertable = false, updatable = false
    private Work work;

    private int hourlyWage;
    private LocalDate contractStart;
    private LocalDate contractEnd;
}
