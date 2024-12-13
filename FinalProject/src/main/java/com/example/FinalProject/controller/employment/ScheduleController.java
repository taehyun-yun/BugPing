package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) boolean viewCompanySchedule // 회사 일정 보기 여부
    ) {
        try {
            // 로그인된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // security 보완 되면 사용
    //      String userId = authentication.getName(); // 로그인된 userId
            String userId = "user111";
            String role = "employer";
            // role 확인  security 보완 되면 사용
            /*boolean isEmployer = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_EMPLOYER"));
            String role = isEmployer ? "ROLE_EMPLOYER" : "ROLE_EMPLOYEE";*/

            System.out.println("User ID: " + userId);
            System.out.println("Role: " + role);

            // security 보완되면 사용
            // 역할 기반 스케줄 조회
            //List<Map<String, Object>> schedules;
            /*if ("employer".equalsIgnoreCase(role) || viewCompanySchedule) {
                // 회사 일정 조회 (employee가 viewCompanySchedule=true인 경우도 포함)
                Integer companyId = scheduleService.getCompanyIdByUserId(userId); // 회사 ID 조회
                if (companyId != null) {
                    schedules = scheduleService.getCompanySchedule(companyId, start, end);
                } else {
                    throw new IllegalStateException("회사 정보를 찾을 수 없습니다.");
                }
            } else {
                // 개인 일정 조회
                schedules = scheduleService.getUserSchedule(userId, start, end);
            }*/
            // 일정 조회 (role에 따라 다르게 처리 가능)
            List<Map<String, Object>> schedules = scheduleService.getCompanySchedule(1, start, end);

            // 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("role", role);
            response.put("schedules", schedules);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }

}

