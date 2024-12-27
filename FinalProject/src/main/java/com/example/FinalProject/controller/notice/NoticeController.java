// NoticeController.java
package com.example.FinalProject.controller.notice;

import com.example.FinalProject.dto.NoticeDTO;
import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.file.File;
import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.notice.FileStorageService;
import com.example.FinalProject.service.notice.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/notice")
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

    /* 모두 조회하기 (companyId 기반) */
    @GetMapping("/list")
    public ResponseEntity<List<NoticeDTO>> getAllNotices(@RequestParam Integer companyId) {
        if (companyId == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<NoticeDTO> notices = noticeService.getAllNoticesAsDTOByCompany(companyId);
        return ResponseEntity.ok(notices);
    }

    /* 타입별 조회하기 (companyId 기반) */
    @GetMapping("/list/type")
    public ResponseEntity<List<NoticeDTO>> getNoticesByType(@RequestParam String type, @RequestParam Integer companyId) {
        if (type == null || companyId == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<NoticeDTO> notices = noticeService.getNoticesByTypeAsDTO(type, companyId);
        return ResponseEntity.ok(notices);
    }
    //workID가져오기, createNotice로 가기전에 workId 추출용임
    @GetMapping("/getWorkIdToGoCreateNotice")
    public ResponseEntity<Map<String,Object>>getWorkId(@RequestParam Integer companyId){
        Map<String,Object>map = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        log.info(" 유저 아이디 확인 : {}", userId);

        Optional<Work> work = workRepository.findTopByUser_userIdAndCompany_CompanyIdOrderByHireDateDesc(userId, companyId);

        log.info(" work 확인 : {}", work);

        if (userId == null || companyId == null) {
            String msg = "userId 또는 companyId가 유효하지 않습니다.";
            map.put("msg", msg);
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if(work.isEmpty()){
            String msg = "일치하는 유저가 회사에 없습니다.";
            map.put("msg",msg);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        map.put("workId",work.get().getWorkId());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /* 공지사항 생성하기 */
    @PostMapping("/create")
    public ResponseEntity<NoticeDTO> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("workId") Integer workId, // 전달된 workId
            @RequestParam("type") String type,
            @RequestParam("companyId") Integer companyId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {

        // workId로 작성자 정보 가져오기
        Work writer = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 작성자 ID입니다."));

        // 작성자의 회사 정보 조회
        Company writerCompany = writer.getCompany();

        if (writerCompany == null) {
            throw new IllegalArgumentException("작성자의 회사 정보가 존재하지 않습니다.");
        }

        // 작성자의 회사 ID와 요청된 회사 ID 비교
        System.out.println("작성자의 회사 ID: " + writerCompany.getCompanyId()); // 디버깅 로그
        System.out.println("전달받은 회사 ID: " + companyId); // 디버깅 로그

        // 작성자의 회사가 전달받은 회사 ID와 일치하는지 검증
        if (!writer.getCompany().getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("작성자의 회사와 전달받은 회사 ID가 일치하지 않습니다.");
        }

        // Notice 객체 생성
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setWork(writer);
        notice.setType(type);
        notice.setStatus("VISIBLE");

        try {
            // 이미지 업로드 처리
            if (image != null && !image.isEmpty()) {
                String imagePath = fileStorageService.storeFile(image);
                File imageFile = new File();
                imageFile.setFilePath(imagePath);
                imageFile.setFileType(image.getContentType());
                notice.addFile(imageFile);
            }

            // 일반 파일 업로드 처리
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

            // 공지사항 저장
            NoticeDTO createdNotice = noticeService.createNoticeAsDTO(notice, companyId);
            return ResponseEntity.ok(createdNotice);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /* 공지사항 삭제 */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteNotices(@RequestBody List<Integer> noticeIds, @RequestParam Integer companyId) {
        noticeService.deleteNotices(noticeIds, companyId);
        return ResponseEntity.noContent().build();
    }

    /* 특정 ID의 공지사항 조회 */
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
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "removedFileIds", required = false) String removedFileIdsJson
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

            // 파일 삭제 처리
            if (removedFileIdsJson != null && !removedFileIdsJson.isEmpty()) {
                List<Integer> removedFileIds = noticeService.parseRemovedFileIds(removedFileIdsJson);
                noticeService.removeFiles(existingNotice, removedFileIds);
            }

            NoticeDTO updatedNotice = noticeService.updatedNoticeAsDTO(existingNotice);
            return ResponseEntity.ok(updatedNotice);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}


