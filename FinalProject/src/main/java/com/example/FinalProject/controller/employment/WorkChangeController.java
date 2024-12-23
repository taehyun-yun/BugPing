package com.example.FinalProject.controller.employment;


import com.example.FinalProject.service.employment.WorkChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api")
public class WorkChangeController {

    @Autowired
    private WorkChangeService workChangeService;

    @PutMapping("/workchange")
    public ResponseEntity<?> updateWorkChange(@RequestBody Map<String, Object> requestData) {
        try {
            Integer scheduleId = Integer.parseInt(requestData.get("scheduleId").toString());
            String newStart = requestData.get("newStart").toString();
            String newEnd = requestData.get("newEnd") != null ? requestData.get("newEnd").toString() : null;

            // 서비스 로직 호출
            workChangeService.updateWorkChange(scheduleId, newStart, newEnd);

            return ResponseEntity.ok("근무 일정이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("근무 일정 변경 중 오류 발생: " + e.getMessage());
        }
    }
}