package com.example.FinalProject.entity.file;

import com.example.FinalProject.entity.notice.Notice;
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
    @Column(name = "file_id")
    private int fileId;

    @ManyToOne
    @JoinColumn(name = "notice_id")
    private Notice notice;

    private String filePath;
    private String fileType;
}
