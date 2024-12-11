package com.example.FinalProject.controller.notice;

import com.example.FinalProject.dto.NoticeDTO;
import com.example.FinalProject.entity.file.File;
import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.FileStorageService;
import com.example.FinalProject.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/notice")
//@CrossOrigin(origins = "http://localhost:8080") // 프론트엔드의 도메인에 맞게 수정 필요
public class NoticeController {

    private final NoticeService noticeService;
    private final WorkRepository workRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public NoticeController(NoticeService noticeService, WorkRepository workRepository, FileStorageService fileStorageService){
        this.noticeService = noticeService;
        this.workRepository = workRepository;
        this.fileStorageService = fileStorageService;
    }

    /* 모두 조회하기 (DTO로 반환) */
    @GetMapping("/list")
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        List<NoticeDTO> notices = noticeService.getAllNoticesAsDTO();
        return ResponseEntity.ok(notices);
    }

    /* 타입별 조회하기 (DTO로 반환) */
    @GetMapping("/list/type")
    public ResponseEntity<List<NoticeDTO>> getNoticesByType(@RequestParam String type) {
        List<NoticeDTO> notices = noticeService.getNoticesByTypeAsDTO(type);
        return ResponseEntity.ok(notices);
    }

    /* 공지사항 생성하기 (파일 포함) */
    @PostMapping("/create")
    public ResponseEntity<NoticeDTO> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("workId") Integer workId,
            @RequestParam("type") String type,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        Work writer = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 작성자 ID입니다."));

        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setWork(writer);
        notice.setType(type);
        notice.setStatus("VISIBLE");

        try {
            // 이미지 업로드
            if (image != null && !image.isEmpty()) {
                String imagePath = fileStorageService.storeFile(image);
                File imageFile = new File();
                imageFile.setFilePath(imagePath);
                imageFile.setFileType(image.getContentType());
                notice.addFile(imageFile);
            }

            // 일반 파일 업로드
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String filePath = fileStorageService.storeFile(file);
                        File uploadedFile = new File();
                        uploadedFile.setFilePath(filePath);
                        uploadedFile.setFileType(file.getContentType());
                        notice.addFile(uploadedFile);
                    }
                }
            }

            NoticeDTO createdNotice = noticeService.createNoticeAsDTO(notice);
            return ResponseEntity.ok(createdNotice);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /* 공지사항 삭제 */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteNotices(@RequestBody List<Integer> noticeIds) {
        noticeService.deleteNotices(noticeIds);
        return ResponseEntity.noContent().build();
    }

    /* 특정 ID의 공지사항 조회 (DTO로 반환) */
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNoticeById(@PathVariable Integer id) {
        NoticeDTO noticeDTO = noticeService.getNoticeByIdAsDTO(id);
        return ResponseEntity.ok(noticeDTO);
    }

    /* 공지사항 업데이트 */
    @PutMapping("/update/{id}")
    public ResponseEntity<NoticeDTO> updateNotice(
            @PathVariable Integer id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("type") String type,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        Notice existingNotice = noticeService.getNoticeById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 공지사항이 존재하지 않습니다."));

        existingNotice.setTitle(title);
        existingNotice.setContent(content);
        existingNotice.setType(type);

        try {
            // 이미지 업로드
            if (image != null && !image.isEmpty()) {
                String imagePath = fileStorageService.storeFile(image);
                File imageFile = new File();
                imageFile.setFilePath(imagePath);
                imageFile.setFileType(image.getContentType());
                existingNotice.addFile(imageFile);
            }

            // 일반 파일 업로드
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String filePath = fileStorageService.storeFile(file);
                        File uploadedFile = new File();
                        uploadedFile.setFilePath(filePath);
                        uploadedFile.setFileType(file.getContentType());
                        existingNotice.addFile(uploadedFile);
                    }
                }
            }

            NoticeDTO updatedNotice = noticeService.updatedNoticeAsDTO(existingNotice);
            return ResponseEntity.ok(updatedNotice);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}