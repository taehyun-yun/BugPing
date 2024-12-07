package com.example.FinalProject.controller.notice;

import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * NoticeController는 공지사항 관련 HTTP 요청을 처리합니다.
 */
@RestController
@RequestMapping("/notice")
@CrossOrigin(origins = "http://localhost:8080") // 프론트엔드의 도메인에 맞게 수정 필요
public class NoticeController {

    private final NoticeService noticeService;
    private final WorkRepository workRepository;

    /**
     * 생성자를 통해 NoticeService와 WorkRepository를 주입받습니다.
     * @param noticeService 공지사항 서비스
     * @param workRepository 작성자(Work) Repository
     */
    @Autowired
    public NoticeController(NoticeService noticeService, WorkRepository workRepository){
        this.noticeService = noticeService;
        this.workRepository = workRepository;
    }

    /**
     * 모든 공지사항을 조회하는 엔드포인트입니다.
     * GET 요청: /notice/list
     * @return 모든 공지사항 목록
     */
    @GetMapping("/list")
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }

    /**
     * 특정 타입의 공지사항을 조회하는 엔드포인트입니다.
     * GET 요청: /notice/list/type?type=NOTICE
     * @param type 공지사항 타입
     * @return 해당 타입의 공지사항 목록
     */
    @GetMapping("/list/type")
    public ResponseEntity<List<Notice>> getNoticesByType(@RequestParam String type) {
        List<Notice> notices = noticeService.getNoticesByType(type);
        return ResponseEntity.ok(notices);
    }

    /**
     * 새로운 공지사항을 생성하는 엔드포인트입니다.
     * POST 요청: /notice/create
     * @param notice 생성할 공지사항 데이터 (work_id 포함)
     * @return 생성된 공지사항 객체
     */
    @PostMapping("/create")
    public ResponseEntity<Notice> createNotice(@RequestBody Notice notice) {
        // 작성자(Work) 정보가 유효한지 확인
        Work writer = workRepository.findById(notice.getWork().getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 작성자 ID입니다."));
        notice.setWork(writer); // 작성자 설정
        Notice createdNotice = noticeService.createNotice(notice);
        return ResponseEntity.ok(createdNotice);
    }

    /**
     * 여러 공지사항을 삭제하는 엔드포인트입니다.
     * DELETE 요청: /notice/delete
     * @param noticeIds 삭제할 공지사항 ID 목록
     * @return 응답 상태
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteNotices(@RequestBody List<Integer> noticeIds) {
        noticeService.deleteNotices(noticeIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 ID의 공지사항을 조회하는 엔드포인트입니다.
     * GET 요청: /notice/{id}
     * @param id 조회할 공지사항 ID
     * @return 조회된 공지사항 객체
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Integer id) {
        return noticeService.getNoticeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 공지사항을 업데이트하는 엔드포인트입니다.
     * PUT 요청: /notice/update
     * @param notice 업데이트할 공지사항 데이터
     * @return 업데이트된 공지사항 객체
     */
    @PutMapping("/update")
    public ResponseEntity<Notice> updateNotice(@RequestBody Notice notice) {
        // 작성자(Work) 정보가 유효한지 확인
        Work writer = workRepository.findById(notice.getWork().getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 작성자 ID입니다."));
        notice.setWork(writer); // 작성자 설정
        Notice updatedNotice = noticeService.updateNotice(notice);
        return ResponseEntity.ok(updatedNotice);
    }
}
