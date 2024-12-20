package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    public ResponseEntity<List<Map<String, Object>>> getSchedules(
            @RequestParam String userId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // UserId 사용자 전체 일정
        List<Map<String, Object>> schedules = scheduleService.getUserSchedule(userId);
        return ResponseEntity.ok(schedules);
    }

}
