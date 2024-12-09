package com.example.FinalProject.entity.employment;

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

    //이거 왜 있는 건지 다시 확인!
//    @ManyToOne
//    @JoinColumn(name = "company_id")
//    private Company company;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")//, nullable = false, insertable = false, updatable = false
    private Work work;

    private int hourlyWage; //오타수정
    private LocalDate contractStart;
    private LocalDate contractEnd;
}
