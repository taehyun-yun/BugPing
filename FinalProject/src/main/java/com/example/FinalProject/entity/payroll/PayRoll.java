package com.example.FinalProject.entity.payroll;

import com.example.FinalProject.entity.work.Work;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class PayRoll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payRollId; // 기본 증가 고유 ID
    private LocalDate paymentDate; // 지급 일자
    private int monthPayment; // 한달 급여
    private int incentive; // 추가 수당
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")
    private Work work; //
}