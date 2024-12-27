// FileStorageService.java
package com.example.FinalProject.service.notice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private final List<String> allowedContentTypes = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/svg+xml",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장 디렉토리를 생성할 수 없습니다: " + uploadDir, ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 파일명 정리
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // 파일명 유효성 검증
        if (originalFileName.contains("..")) {
            throw new RuntimeException("잘못된 파일 경로: " + originalFileName);
        }

        // 파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            throw new RuntimeException("허용되지 않는 파일 타입: " + contentType);
        }

        // 파일명 고유성 확보
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + uniqueFileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("파일을 삭제할 수 없습니다: " + fileName, ex);
        }
    }
}
