// NoticeServiceImpl.java
package com.example.FinalProject.service.notice;

import com.example.FinalProject.dto.FileDTO;
import com.example.FinalProject.dto.NoticeDTO;
import com.example.FinalProject.dto.WorkDTO;
import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.file.File;
import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.notice.FileRepository;
import com.example.FinalProject.repository.notice.NoticeRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * NoticeServiceImpl 클래스는 NoticeService 인터페이스를 구현하며,
 * 실제 공지사항 관련 비즈니스 로직을 수행합니다.
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final CompanyRepository companyRepository;

    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository, FileRepository fileRepository, FileStorageService fileStorageService, ObjectMapper objectMapper, CompanyRepository companyRepository) {
        this.noticeRepository = noticeRepository;
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    @Override
    public List<Notice> getNoticesByType(String type) {
        return noticeRepository.findByType(type);
    }

    @Override
    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    @Override
    public void deleteNotices(List<Integer> noticeIds, Integer companyId) {
        for (Integer noticeId : noticeIds) {
            Notice notice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + noticeId));

            // 회사 검증
            if (!notice.getWork().getCompany().getCompanyId().equals(companyId)) {
                throw new IllegalArgumentException("해당 공지사항은 삭제할 권한이 없습니다: " + noticeId);
            }

            // Notice와 연결된 파일 삭제
            for (File file : notice.getFiles()) {
                fileStorageService.deleteFile(file.getFilePath());
            }
            notice.getFiles().clear();
            noticeRepository.delete(notice);
        }
    }

    @Override
    public Optional<Notice> getNoticeById(Integer id) {
        return noticeRepository.findById(id);
    }

    @Override
    public Notice updatedNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    @Override
    public NoticeDTO getNoticeByIdAsDTO(Integer id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));
        return convertToDTO(notice);
    }

    @Override
    public NoticeDTO createNoticeAsDTO(Notice notice, Integer companyId) {
        // 1. companyId를 기반으로 회사 정보를 가져옴
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if (companyOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid companyId: " + companyId);
        }
        Company company = companyOptional.get();

        // 2. 작성자의 회사 정보 확인
        Work work = notice.getWork();
        if (work == null || work.getCompany() == null) {
            throw new IllegalArgumentException("작성자의 회사 정보가 존재하지 않습니다.");
        }

        Company writerCompany = work.getCompany();

        // 3. 작성자의 회사 ID와 요청된 회사 ID 비교
        if (!writerCompany.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("작성자의 회사와 전달받은 회사 ID가 일치하지 않습니다.");
        }

        // 4. Notice 저장
        Notice savedNotice = noticeRepository.save(notice);

        // 5. 저장된 Notice를 DTO로 변환 후 반환
        return convertToDTO(savedNotice);
    }

//    @Override
//    public List<NoticeDTO> getAllNoticesAsDTOByCompany(Integer companyId) {
//        List<Notice> notices = noticeRepository.findByWork_Company_CompanyId(companyId);
//
//        // 디버깅 로그 추가
//        System.out.println("companyId: " + companyId);
//        System.out.println("조회된 공지사항 개수: " + notices.size());
//        notices.forEach(notice -> System.out.println("Notice ID: " + notice.getNoticeId()));
//
//        return notices.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }


    @Override
    public List<NoticeDTO> getNoticesByTypeAsDTO(String type, Integer companyId) {
        List<Notice> notices = noticeRepository.findByTypeAndWork_Company_CompanyId(type, companyId);

        // 디버깅 로그 추가
        System.out.println("요청된 Company ID: " + companyId);
        System.out.println("요청된 글 타입 : " + type);
        System.out.println("조회된 게시글 수: " + notices.size());
        notices.forEach(notice -> {
            System.out.println("게시글 ID: " + notice.getNoticeId() + ", Company ID: " +
                    (notice.getWork() != null && notice.getWork().getCompany() != null
                            ? notice.getWork().getCompany().getCompanyId() : "null"));
        });

        return notices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

//    @Override
//    public NoticeDTO createNoticeAsDTO(Notice notice) {
//        Notice savedNotice = noticeRepository.save(notice);
//        return convertToDTO(savedNotice);
//    }

    @Override
    public NoticeDTO updatedNoticeAsDTO(Notice notice) {
        Notice updatedNotice = noticeRepository.save(notice);
        return convertToDTO(updatedNotice);
    }

    @Override
    @Transactional
    public void removeFiles(Notice notice, List<Integer> removedFileIds) {
        for (Integer fileId : removedFileIds) {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("파일 ID가 유효하지 않습니다: " + fileId));
            // Remove file from notice
            notice.removeFile(file);
            // Delete file from storage
            fileStorageService.deleteFile(file.getFilePath());
            // Delete file record from database
            fileRepository.delete(file);
        }
        noticeRepository.save(notice);
    }

    @Override
    public List<Integer> parseRemovedFileIds(String removedFileIdsJson) {
        try {
            return objectMapper.readValue(removedFileIdsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("removedFileIds 파싱 오류", e);
        }
    }

    @Override
    public List<NoticeDTO> getAllNoticesAsDTOByCompany(Integer companyId) {
        return List.of();
    }


    private NoticeDTO convertToDTO(Notice notice) {
        // Notice 엔티티의 데이터를 NoticeDTO로 변환
        NoticeDTO noticeDTO = new NoticeDTO();

        // Notice 필드 매핑
        noticeDTO.setNoticeId(notice.getNoticeId());
        noticeDTO.setTitle(notice.getTitle());
        noticeDTO.setContent(notice.getContent());
        noticeDTO.setStatus(notice.getStatus());
        noticeDTO.setType(notice.getType());
        noticeDTO.setCreatedAt(notice.getCreatedAt());
        noticeDTO.setUpdatedAt(notice.getUpdatedAt());

        // Work 매핑
        Work work = notice.getWork();
        if (work != null) {
            WorkDTO workDTO = new WorkDTO();
            workDTO.setWorkId(work.getWorkId());
            workDTO.setUserId(work.getUser() != null ? work.getUser().getUserId() : null); // User ID 설정
            workDTO.setUserName(work.getUser() != null ? work.getUser().getName() : null); // User 이름 설정
            workDTO.setHireDate(work.getHireDate());
            workDTO.setResignDate(work.getResignDate());
            noticeDTO.setWork(workDTO);
        }

        // Company ID 설정
        if (work != null && work.getCompany() != null) {
            noticeDTO.setCompanyId(work.getCompany().getCompanyId());
        }

        // File 매핑
        if (notice.getFiles() != null && !notice.getFiles().isEmpty()) {
            List<FileDTO> fileDTOs = notice.getFiles().stream().map(file -> {
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFileId(file.getFileId());
                fileDTO.setFilePath(file.getFilePath());
                fileDTO.setFileType(file.getFileType());
                return fileDTO;
            }).collect(Collectors.toList());
            noticeDTO.setFiles(fileDTOs);
        }

        return noticeDTO;
    }
}
