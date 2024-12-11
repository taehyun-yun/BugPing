package com.example.FinalProject.controller.employment;

import com.example.FinalProject.dto.ScheduleDTO;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @RequestParam String userId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
//      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // UserId 사용자 전체 일정
        List<Map<String, Object>> schedules = scheduleService.getUserSchedule(userId);
        return ResponseEntity.ok(schedules);
    }

}
