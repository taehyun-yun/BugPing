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
            @RequestParam(required = false) boolean viewCompanySchedule // 회사 일정 보기 여부
    ) {
        try {
            // 로그인된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


            String userId = authentication.getName(); // 로그인된 userId

            // role 확인
//            boolean isEmployer = authentication.getAuthorities().stream()
//                    .anyMatch(authority -> "ROLE_EMPLOYER".equals(authority.getAuthority()));
//            String role = isEmployer ? "ROLE_EMPLOYER" : "ROLE_EMPLOYEE";
            boolean isEmployer = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_EMPLOYER"));
            String role = isEmployer ? "ROLE_EMPLOYER" : "ROLE_EMPLOYEE";
            authentication.getAuthorities().forEach(System.out::println);

            System.out.println("현재의 userId : " +userId);
            System.out.println("현재의 role : " +role);
            // 역할 기반 스케줄 조회

                // 회사 일정 조회
                Integer companyId = scheduleService.getCompanyIdByUserId(userId); // 회사 ID 조회
                if (companyId == null) {
                    throw new IllegalStateException("회사 정보를 찾을 수 없습니다.");
                }

                // 역할 및 보기 옵션에 따른 스케줄 처리 (employer이거나,
                List<Map<String, Object>> schedules;
                if ("ROLE_EMPLOYER".equalsIgnoreCase(role) || viewCompanySchedule) {
                    // 회사 일정 조회
                    schedules = scheduleService.getUserSchedule(userId, start, end);
                } else {
                    // 개인, 회사 일정 조회
                    //List<Map<String, Object>> userSchedules = scheduleService.getUserSchedule(userId, start, end);
                    List<Map<String, Object>> companySchedules = scheduleService.getCompanySchedule(companyId, start, end);
                    schedules = new ArrayList<>();
                    //schedules.addAll(userSchedules);
                    schedules.addAll(companySchedules);
                }

            // 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("role", role);
            response.put("schedules", schedules);
            response.put("companyId", companyId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }

}

