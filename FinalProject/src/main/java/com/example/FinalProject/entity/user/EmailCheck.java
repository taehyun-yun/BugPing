package com.example.FinalProject.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class EmailCheck {
    @Id
    private String email;
    private String code;
    private LocalDateTime due;
}
