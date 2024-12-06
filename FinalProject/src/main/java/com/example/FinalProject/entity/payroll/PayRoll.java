package com.example.FinalProject.entity.payroll;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;

    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "tax_amount", nullable = false)
    private BigDecimal taxAmount;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    // 연관 관계 설정 (예: Employee 엔티티와 연관 관계)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
//    private Employee employee;
}
