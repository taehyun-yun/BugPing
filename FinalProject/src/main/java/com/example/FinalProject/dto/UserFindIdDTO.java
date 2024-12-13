package com.example.FinalProject.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserFindIdDTO {
    private String userId;
    private LocalDate regDate;
}
