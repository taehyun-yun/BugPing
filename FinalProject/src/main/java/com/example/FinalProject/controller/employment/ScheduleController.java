package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/calendar")
    public ResponseEntity<?> getSchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false, defaultValue = "false") boolean viewCompanySchedule,
            @RequestParam(required = false) Integer companyId // 선택된 회사 ID
    ) {
        try {
            // 현재 사용자 ID 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();


            // 사용자 역할 확인
            boolean isEmployer = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_EMPLOYER"));
            String role = isEmployer ? "ROLE_EMPLOYER" : "ROLE_EMPLOYEE";

            // 사용자 회사 ID 목록 조회
            List<Integer> companyIds = scheduleService.getCompanyIdsByUserId(userId);
            if (companyIds.isEmpty()) {
                throw new IllegalStateException("사용자가 속한 회사가 없습니다.");
            }

            // 선택된 회사 ID 검증
            if (companyId != null && !companyIds.contains(companyId)) {
                return ResponseEntity.badRequest().body("선택된 회사 ID는 사용자가 속한 회사가 아닙니다.");
            }

            // 선택된 회사 ID가 없는 경우 기본값 설정
            if (companyId == null) {
                System.out.println("회사가 없어여 : " + companyId);
                companyId = companyIds.get(0); // 첫 번째 회사 ID로 기본 설정
            }

            // 스케줄 조회
            List<Map<String, Object>> schedules = scheduleService.getSchedulesByRole(
                    userId, role, start, end, viewCompanySchedule, companyId
            );

            // 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("role", role);
            response.put("selectedCompanyId", companyId);
            response.put("companyIds", companyIds); // 사용자 회사 ID 목록 반환
            response.put("schedules", schedules);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("요청 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("잘못된 요청: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }
}