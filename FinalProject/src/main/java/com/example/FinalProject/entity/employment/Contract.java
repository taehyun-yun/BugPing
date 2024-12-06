package com.example.FinalProject.entity.employment;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contractId", nullable = false, updatable = false)
    private Integer contractId;

//    // Relationships
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "workId", nullable = false) //insertable = false, updatable = false
//    private Working working;

    @Column(name = "hourlyWage")
    private Integer hourlyWage;

    @Column(name = "contractStart")
    private LocalDateTime contractStart;

    @Column(name = "contractEnd")
    private LocalDateTime contractEnd;

    @Column(name = "userId", nullable = false)
    private Integer userId;



}