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
    private String userId;
    private String password;
    private String name;
    private String tel;
    private LocalDate birth;
    private String gender;
    private String role;
    private LocalDate regDate;
    private String email;
}