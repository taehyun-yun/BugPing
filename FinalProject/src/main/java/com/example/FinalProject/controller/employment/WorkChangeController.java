package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.example.FinalProject.service.employment.WorkChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WorkChangeController {

    @Autowired
    private WorkChangeService workChangeService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private WorkChangeRepository workChangeRepository;

    // 근무 교대 요청 생성 (드래그 앤 드롭 처리 추가)
    @PostMapping("/workchange")
    public ResponseEntity<?> createWorkChange(@RequestBody Map<String, Object> updatedEvent) {
        try {
            // 클라이언트에서 보낸 데이터 파싱
            Integer originalScheduleId = (Integer) updatedEvent.get("originalScheduleId");
            String originalDate = (String) updatedEvent.get("originalDate");
            Integer newScheduleId = (Integer) updatedEvent.get("newScheduleId");
            String newDate = (String) updatedEvent.get("newDate");

            // 필수 데이터 검증
            if (originalScheduleId == null || originalDate == null || newScheduleId == null || newDate == null) {
                throw new IllegalArgumentException("필수 요청 데이터가 누락되었습니다.");
            }

            // WorkChangeService를 호출하여 스케줄 변경 처리
            workChangeService.updateWorkChange(
                    originalScheduleId,
                    LocalDate.parse(originalDate),
                    newScheduleId,
                    LocalDate.parse(newDate)
            );

            // 응답 데이터 생성
            Map<String, Object> response = new HashMap<>();
            response.put("message", "스케줄이 성공적으로 변경되었습니다.");
            response.put("originalScheduleId", originalScheduleId);
            response.put("newScheduleId", newScheduleId);
            response.put("originalDate", originalDate);
            response.put("newDate", newDate);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

