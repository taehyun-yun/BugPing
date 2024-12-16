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
            @RequestParam(required = false, defaultValue = "false") boolean viewCompanySchedule // 추가된 파라미터
    ) {
        try {
            // 로그인된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName(); // 로그인된 userId

            // 사용자 역할 확인
            boolean isEmployer = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_EMPLOYER"));
            String role = isEmployer ? "ROLE_EMPLOYER" : "ROLE_EMPLOYEE";

            System.out.println("현재의 userId : " + userId);
            System.out.println("현재의 role : " + role);

            // 회사 ID 조회
            Integer companyId = scheduleService.getCompanyIdByUserId(userId);
            if (companyId == null) {
                throw new IllegalStateException("회사 정보를 찾을 수 없습니다.");
            }

            // 역할에 따른 스케줄 조회
            List<Map<String, Object>> schedules = new ArrayList<>();
            if (isEmployer) {
                // ROLE_EMPLOYER 사용자는 항상 회사 전체 일정 반환
                schedules = scheduleService.getCompanySchedule(companyId, start, end);
            } else {
                // ROLE_EMPLOYEE 사용자는 viewCompanySchedule에 따라 분기
                if (viewCompanySchedule) {
                    schedules = scheduleService.getCompanySchedule(companyId, start, end);
                } else {
                    schedules = scheduleService.getUserSchedule(userId, start, end);
                }
            }

            // 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("role", role);
            response.put("companyId", companyId);
            response.put("schedules", schedules);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }
}


