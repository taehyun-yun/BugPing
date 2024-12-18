package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.example.FinalProject.service.employment.WorkChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WorkChangeController {

    @Autowired
    private WorkChangeService workChangeService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private WorkChangeRepository workChangeRepository;

    // 근무 교대 요청 생성
    @PostMapping("/workchange")
    public ResponseEntity<?> createWorkChange(@RequestBody WorkChange workChange) {
        try {
            // Schedule을 가져오기
            Schedule schedule = scheduleRepository.findById(workChange.getSchedule().getScheduleId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));

            // 기존 시간 유지
            LocalTime originalStartTime = schedule.getOfficialStart();
            LocalTime originalEndTime = schedule.getOfficialEnd();

            // 새로운 WorkChange 설정
            workChange.setChangeStartTime(workChange.getChangeDate().atTime(originalStartTime));
            workChange.setChangeEndTime(workChange.getChangeDate().atTime(originalEndTime));
            workChange.setSchedule(schedule);
            workChange.setPermit(true);
            workChange.setStatus("변경됨");

            workChangeRepository.save(workChange);

            // 변경된 데이터를 반환
            Map<String, Object> response = new HashMap<>();
            response.put("scheduleId", workChange.getSchedule().getScheduleId());
            response.put("title", "변경된 근무");
            response.put("start", workChange.getChangeStartTime().toString()); // ISO 형식
            response.put("end", workChange.getChangeEndTime().toString()); // ISO 형식
            response.put("description", "근무 일정이 변경되었습니다.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
