package com.example.FinalProject.controller.notice;

import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notice")
//@CrossOrigin(origins = "http://localhost:8080") // 프론트엔드의 도메인에 맞게 수정 필요
public class NoticeController {

    private final NoticeService noticeService;
    private final WorkRepository workRepository;

    @Autowired
    public NoticeController(NoticeService noticeService, WorkRepository workRepository){
        this.noticeService = noticeService;
        this.workRepository = workRepository;
    }

    /* 모두 조회하기 @return 모든 공지사항 목록 */
    @GetMapping("/list")
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }

    /* 타입별 조회하기 */
    @GetMapping("/list/type")
    public ResponseEntity<List<Notice>> getNoticesByType(@RequestParam String type) {
        List<Notice> notices = noticeService.getNoticesByType(type);
        return ResponseEntity.ok(notices);
    }

    /* 공지사항 생성하기 @param notice 생성할 공지사항 데이터 (work_id 포함) @return 생성된 공지사항 객체 */
    @PostMapping("/create")
    public ResponseEntity<Notice> createNotice(@RequestBody Notice notice) {
        Work writer = workRepository.findById(notice.getWork().getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 작성자 ID입니다."));
        notice.setWork(writer);

        // type 값이 없으면 기본값 설정
        if (notice.getType() == null || notice.getType().isEmpty()) {
            notice.setType("NOTICE");
        }

        Notice createdNotice = noticeService.createNotice(notice);
        return ResponseEntity.ok(createdNotice);
    }
    /* 공지사항 삭제하하기 */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteNotices(@RequestBody List<Integer> noticeIds) {
        noticeService.deleteNotices(noticeIds);
        return ResponseEntity.noContent().build();
    }

    /* 특정 ID의 공지사항을 조회하기 */
    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Integer id) {
        return noticeService.getNoticeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* 공지사항 업데이트 */
    @PutMapping("/update/{id}")
    public ResponseEntity<Notice> updateNotice(@PathVariable Integer id, @RequestBody Notice notice) {
        // ID로 기존 공지사항 조회
        Notice existingNotice = noticeService.getNoticeById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 공지사항이 존재하지 않습니다."));

        // 수정할 내용 업데이트
        existingNotice.setTitle(notice.getTitle());
        existingNotice.setContent(notice.getContent());
        existingNotice.setStatus(notice.getStatus());
        existingNotice.setType(notice.getType());

        // 작성자 정보 업데이트 (필요하다면)
        if (notice.getWork() != null && notice.getWork().getWorkId() != null) {
            Work writer = workRepository.findById(notice.getWork().getWorkId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 작성자 ID입니다."));
            existingNotice.setWork(writer);
        }

        // 업데이트된 공지사항 저장
        Notice updated = noticeService.updatedNotice(existingNotice);
        return ResponseEntity.ok(updated);
    }
}
