package com.example.FinalProject.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class User {
    @Id
    @Column(name = "userId")
    private String userId;
    private String password;
    private String name;
    private String tel;
    private LocalDate birth;
    private String gender;
    private String role;
    @Column(name = "regDate")
    private LocalDate regDate;
}