package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false, defaultValue = "false") boolean viewCompanySchedule
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            boolean isEmployer = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_EMPLOYER"));
            String role = isEmployer ? "ROLE_EMPLOYER" : "ROLE_EMPLOYEE";

            Integer companyId = scheduleService.getCompanyIdByUserId(userId);
            if (companyId == null) {
                throw new IllegalStateException("회사 정보를 찾을 수 없습니다.");
            }

            List<Map<String, Object>> schedules;
            if (isEmployer) {
                schedules = scheduleService.getCompanySchedule(companyId, start, end);
            } else {
                if (viewCompanySchedule) {
                    schedules = scheduleService.getCompanySchedule(companyId, start, end);
                } else {
                    schedules = scheduleService.getUserSchedule(userId, start, end);
                }
            }

            // 각 스케줄에 scheduleId 추가
            List<Map<String, Object>> updatedSchedules = new ArrayList<>();
            for (Map<String, Object> schedule : schedules) {
                Map<String, Object> updatedSchedule = new HashMap<>(schedule);
                updatedSchedule.put("scheduleId", schedule.get("scheduleId")); // scheduleId 추가
                updatedSchedules.add(updatedSchedule);
            }

            // 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("role", role);
            response.put("companyId", companyId);
            response.put("schedules", updatedSchedules); // 수정된 schedules 반환

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }

}


