package com.example.FinalProject.entity.notice;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;
    @ManyToOne
    @JoinColumn(name = "noticeId")
    private Notice notice;
    private String filepath;
    private String filetype;
}
