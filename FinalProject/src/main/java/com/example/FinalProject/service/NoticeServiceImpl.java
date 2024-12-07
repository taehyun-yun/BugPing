package com.example.FinalProject.service;

import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.repository.notice.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * NoticeServiceImpl 클래스는 NoticeService 인터페이스를 구현하며,
 * 실제 공지사항 관련 비즈니스 로직을 수행합니다.
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 생성자를 통해 NoticeRepository를 주입받습니다.
     * @param noticeRepository 공지사항 Repository
     */
    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /**
     * 모든 공지사항을 데이터베이스에서 조회합니다.
     * @return 공지사항 목록
     */
    @Override
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    /**
     * 특정 타입의 공지사항을 데이터베이스에서 조회합니다.
     * @param type 공지사항 타입
     * @return 해당 타입의 공지사항 목록
     */
    @Override
    public List<Notice> getNoticesByType(String type) {
        return noticeRepository.findByType(type);
    }

    /**
     * 새로운 공지사항을 데이터베이스에 저장합니다.
     * @param notice 저장할 공지사항 객체
     * @return 저장된 공지사항 객체
     */
    @Override
    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    /**
     * 여러 공지사항을 한 번에 삭제합니다.
     * @param noticeIds 삭제할 공지사항 ID 목록
     */
    @Override
    public void deleteNotices(List<Integer> noticeIds) {
        noticeRepository.deleteAllById(noticeIds);
    }

    /**
     * 특정 ID의 공지사항을 조회합니다.
     * @param id 조회할 공지사항 ID
     * @return 조회된 공지사항 객체 Optional로 반환
     */
    @Override
    public Optional<Notice> getNoticeById(Integer id) {
        return noticeRepository.findById(id);
    }

    /**
     * 공지사항을 업데이트하여 데이터베이스에 저장합니다.
     * @param notice 업데이트할 공지사항 객체
     * @return 업데이트된 공지사항 객체
     */
    @Override
    public Notice updateNotice(Notice notice) {
        return noticeRepository.save(notice);
    }
}
