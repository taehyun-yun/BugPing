package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/schedules")
    public ResponseEntity<List<Map<String, Object>>> getSchedules(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {

        if (userId != null) {
            // 특정 사용자 일정 조회
            List<Map<String, Object>> schedules = scheduleService.getUserSchedule(userId, start, end);
            return ResponseEntity.ok(schedules);
        } else if (companyId != null) {
            // 특정 회사 일정 조회
            List<Map<String, Object>> schedules = scheduleService.getCompanySchedule(companyId, start, end);
            return ResponseEntity.ok(schedules);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

