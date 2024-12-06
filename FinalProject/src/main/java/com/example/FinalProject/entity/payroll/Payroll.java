package com.example.FinalProject.entity.payroll;

import com.example.FinalProject.entity.working.Work;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PayRoll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payRollId;
    private LocalDate paymentDate;
    private int monthPayment;
    private int incentive;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workId")
    private Work work;
}
