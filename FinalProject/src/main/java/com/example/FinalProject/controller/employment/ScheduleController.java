package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/calendar")
    public ResponseEntity<?> getSchedules(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {

            List<Map<String, Object>> schedules;

            if (userId != null) {
                // 사용자 일정 조회
                schedules = scheduleService.getUserSchedule(userId, start, end);
            } else if (companyId != null) {
                // 회사 일정 조회
                schedules = scheduleService.getCompanySchedule(companyId, start, end);
            } else {
                return ResponseEntity.badRequest().body("userId 또는 companyId를 제공해야 합니다.");
            }

            return ResponseEntity.ok(schedules);

        } catch (IllegalArgumentException e) {
            // 잘못된 요청 처리
            return ResponseEntity.badRequest().body("요청 처리 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            // 일반적인 서버 오류 처리
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }

}

