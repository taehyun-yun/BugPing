package com.example.FinalProject.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoticeDTO {
    private Integer noticeId;
    private String title;
    private String content;
    private String status;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private WorkDTO work;
    private List<FileDTO> files;
    private Integer companyId; // 추가: companyId
}