package com.example.FinalProject.service;

import com.example.FinalProject.dto.FileDTO;
import com.example.FinalProject.dto.NoticeDTO;
import com.example.FinalProject.dto.WorkDTO;
import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.notice.NoticeRepository;
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

        public NoticeServiceImpl(NoticeRepository noticeRepository) {
            this.noticeRepository = noticeRepository;
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
        public void deleteNotices(List<Integer> noticeIds) {
            noticeRepository.deleteAllByIdInBatch(noticeIds);
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
        public List<NoticeDTO> getAllNoticesAsDTO() {
            return noticeRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        @Override
        public List<NoticeDTO> getNoticesByTypeAsDTO(String type) {
            return noticeRepository.findByType(type).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        @Override
        public NoticeDTO createNoticeAsDTO(Notice notice) {
            Notice savedNotice = noticeRepository.save(notice);
            return convertToDTO(savedNotice);
        }

        @Override
        public NoticeDTO updatedNoticeAsDTO(Notice notice) {
            Notice updatedNotice = noticeRepository.save(notice);
            return convertToDTO(updatedNotice);
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
                if (work.getUser() != null) {
                    workDTO.setUserId(work.getUser().getUserId());
                    workDTO.setUserName(work.getUser().getName());
                }
                workDTO.setHireDate(work.getHireDate());
                workDTO.setResignDate(work.getResignDate());
                noticeDTO.setWork(workDTO);
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
