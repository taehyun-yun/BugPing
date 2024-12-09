package com.example.FinalProject.service;

import com.example.FinalProject.entity.notice.Notice;

import java.util.List;
import java.util.Optional;

/**
 * NoticeService 인터페이스는 공지사항 관련 비즈니스 로직을 정의합니다.
 */
public interface NoticeService {
    /**
     * 모든 공지사항을 조회합니다.
     * @return 공지사항 목록
     */
    List<Notice> getAllNotices();

    /**
     * 특정 타입의 공지사항을 조회합니다.
     * @param type 공지사항 타입 (NOTICE, MANUAL, SPECIAL)
     * @return 해당 타입의 공지사항 목록
     */
    List<Notice> getNoticesByType(String type);

    /**
     * 새로운 공지사항을 생성합니다.
     * @param notice 생성할 공지사항 객체
     * @return 생성된 공지사항 객체
     */
    Notice createNotice(Notice notice);

    /**
     * 여러 공지사항을 삭제합니다.
     * @param noticeIds 삭제할 공지사항의 ID 목록
     */
    void deleteNotices(List<Integer> noticeIds);

    /**
     * 특정 ID의 공지사항을 조회합니다.
     * @param id 조회할 공지사항 ID
     * @return 조회된 공지사항 객체 Optional로 반환
     */
    Optional<Notice> getNoticeById(Integer id);

    /**
     * 공지사항을 업데이트합니다.
     * @param notice 업데이트할 공지사항 객체
     * @return 업데이트된 공지사항 객체
     */
    Notice updatedNotice(Notice notice);
}
