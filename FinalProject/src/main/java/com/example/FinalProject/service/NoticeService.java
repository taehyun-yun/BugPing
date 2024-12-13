// NoticeService.java
package com.example.FinalProject.service;

import com.example.FinalProject.dto.NoticeDTO;
import com.example.FinalProject.entity.notice.Notice;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * NoticeService 인터페이스는 공지사항 관련 비즈니스 로직을 정의합니다.
 */
@Service
public interface NoticeService {

    List<Notice> getAllNotices();

    List<Notice> getNoticesByType(String type);

    Notice createNotice(Notice notice);

    void deleteNotices(List<Integer> noticeIds);

    Optional<Notice> getNoticeById(Integer id);

    Notice updatedNotice(Notice notice);

    // DTO 반환 메서드
    NoticeDTO getNoticeByIdAsDTO(Integer id);

    List<NoticeDTO> getAllNoticesAsDTO();

    List<NoticeDTO> getNoticesByTypeAsDTO(String type);

    NoticeDTO createNoticeAsDTO(Notice notice);

    NoticeDTO updatedNoticeAsDTO(Notice notice);

    // 파일 삭제 관련 메서드
    void removeFiles(Notice notice, List<Integer> removedFileIds);

    List<Integer> parseRemovedFileIds(String removedFileIdsJson);
}
