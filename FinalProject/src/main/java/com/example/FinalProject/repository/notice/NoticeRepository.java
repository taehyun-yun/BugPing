package com.example.FinalProject.repository.notice;

import com.example.FinalProject.entity.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * NoticeRepository는 Notice 엔티티에 대한 CRUD 작업을 지원합니다.
 */
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    /**
     * 특정 타입의 공지사항을 조회합니다.
     * @param type 공지사항 타입
     * @return 해당 타입의 공지사항 목록
     */
    List<Notice> findByType(String type);
    List<Notice> findByStatus(String status);
}
