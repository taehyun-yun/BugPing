package com.example.FinalProject.entity.notice;

import com.example.FinalProject.entity.working.Work;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeId;
    @JoinColumn(name = "work_id")
    @ManyToOne
    private Work work;
    private String title;
    @Column(length = 10000)
    private String content;
    private LocalDate createAt;
    private LocalDate updateAt;
    private String status;
    private String type;
}
