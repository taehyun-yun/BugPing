package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.WorkChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class WorkChangeController {

    @Autowired
    private WorkChangeService workChangeService;

    @PostMapping("/workchange")
    public ResponseEntity<?> createWorkChange(@RequestBody Map<String, Object> updatedEvent) {
        try {
            System.out.println("요청 데이터: " + updatedEvent);

            Integer originalScheduleId = Integer.parseInt(updatedEvent.get("originalScheduleId").toString());
            String originalDate = updatedEvent.get("originalDate").toString();
            Integer newScheduleId = Integer.parseInt(updatedEvent.get("newScheduleId").toString());
            String newDate = updatedEvent.get("newDate").toString();

            // WorkChangeService를 호출하여 변경 처리
            workChangeService.updateWorkChange(
                    originalScheduleId,
                    LocalDate.parse(originalDate),
                    newScheduleId,
                    LocalDate.parse(newDate)
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "근무 변경이 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
