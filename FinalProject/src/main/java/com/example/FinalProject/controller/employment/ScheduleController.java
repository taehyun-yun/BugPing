package com.example.FinalProject.controller.employment;

import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            //@RequestParam(required = false) String userId,
            //@RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            // 로그인한 UserId 가져오기
          //  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String userId = "user111";     //로그인 아이디
            System.out.println("userId : "+ userId);

            // 사용자와 연결된 companyId 가져오기
            Integer companyId = scheduleService.getCompanyIdByUserId(userId);
            System.out.println("company ID: " + companyId);

            Map<String, Object> response = new HashMap<>();
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

            response.put("userId", userId);
            response.put("schedules", schedules);


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }

}

