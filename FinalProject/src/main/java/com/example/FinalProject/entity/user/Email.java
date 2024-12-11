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
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer emailId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String code;
    private LocalDateTime sendTime;
}
