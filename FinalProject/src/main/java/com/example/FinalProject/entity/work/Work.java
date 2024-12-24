package com.example.FinalProject.entity.work;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workId;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate hireDate;
    private LocalDate resignDate;//null일 땐 아직 일하고 있다는 뜻
}
