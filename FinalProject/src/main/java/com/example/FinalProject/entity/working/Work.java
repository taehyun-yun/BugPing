package com.example.FinalProject.entity.working;

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
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workingId;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private LocalDate hireDate;
    private LocalDate resignDate;

}
