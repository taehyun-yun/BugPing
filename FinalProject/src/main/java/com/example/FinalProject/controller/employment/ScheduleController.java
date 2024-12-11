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
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getSchedules(
            @RequestParam String userId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // UserId 사용자 전체 일정
        List<Map<String, Object>> schedules = scheduleService.getUserSchedule(userId);
        return ResponseEntity.ok(schedules);
    }

    // 페이징 처리된 전체 스케줄
    @GetMapping("/paged")
    public ResponseEntity<Page<Map<String, Object>>> getPagedSchedules(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            /*@RequestParam(required = false) String start,
            @RequestParam(required = false) String end,*/
            @RequestParam int page,
            @RequestParam int size) {

        Page<Map<String, Object>> pagedSchedules = scheduleService.getPagedSchedulesByDateRange(start, end, page, size);
        return ResponseEntity.ok(pagedSchedules);
    }
}
