//package com.example.FinalProject.controller.notice;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@RestController
//@RequestMapping("/notice/files")
//public class FileController {
//
//    private final Path fileStoragePath = Paths.get("C:/uploads").toAbsolutePath().normalize();
//
//    @GetMapping("/{fileName:.+}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
//        try {
//            // 파일 경로 설정
//            Path filePath = fileStoragePath.resolve(fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//            } else {
//                throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다: " + fileName);
//            }
//        } catch (Exception ex) {
//            throw new RuntimeException("파일을 로드하는 동안 오류가 발생했습니다: " + fileName, ex);
//        }
//    }
//}
