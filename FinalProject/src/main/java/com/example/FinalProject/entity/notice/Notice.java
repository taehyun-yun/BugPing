package com.example.FinalProject.entity.notice;

import com.example.FinalProject.entity.file.File;
import com.example.FinalProject.entity.work.Work;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Notice 엔티티는 공지사항 정보를 저장합니다.
 * 작성자는 Work 엔티티와의 다대일 관계로 관리됩니다.
 */
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

    /**
     * 작성자 정보를 참조하는 필드입니다.
     * Work 엔티티와 다대일 관계를 맺습니다.
     */
    @ManyToOne
    @JoinColumn(name = "work_id", nullable = false) // work_id는 공지사항의 작성자를 의미
    private Work work;

    private String title; // 공지사항 제목

    @Column(length = 10000)
    private String content; // 공지사항 내용

    private LocalDateTime createdAt; // 생성 날짜
    private LocalDateTime updatedAt; // 수정 날짜

    private String status; // 공지사항 상태 (VISIBLE, DRAFT, WITHDRAWN)
    private String type; // 공지사항 타입 (NOTICE, MANUAL, SPECIAL)

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    /**
     * 공지사항이 처음 생성될 때 생성 날짜와 수정 날짜를 자동으로 설정합니다.
     */

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 공지사항이 업데이트될 때 수정 날짜를 자동으로 갱신합니다.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 파일 추가 메서드
    public void addFile(File file) {
        files.add(file);
        file.setNotice(this);
    }

    // 파일 제거 메서드
    public void removeFile(File file) {
        files.remove(file);
        file.setNotice(null);
    }
}
