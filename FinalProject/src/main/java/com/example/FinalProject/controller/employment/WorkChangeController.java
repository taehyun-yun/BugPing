package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.example.FinalProject.service.employment.WorkChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    // 근무 교대 요청 생성 (드래그 앤 드롭 처리 추가

    @PostMapping("/workchange")
    public ResponseEntity<?> createWorkChange(@RequestBody Map<String, Object> updatedEvent) {
        try {
            System.out.println("요청 데이터: " + updatedEvent);

            Integer originalScheduleId = Integer.parseInt(updatedEvent.get("originalScheduleId").toString());
            String originalDate = updatedEvent.get("originalDate").toString();
            Integer newScheduleId = Integer.parseInt(updatedEvent.get("newScheduleId").toString());
            String newDate = updatedEvent.get("newDate").toString();


            // UTC 시간 형식 파싱
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            LocalDateTime startTime = LocalDateTime.parse(updatedEvent.get("startTime").toString(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(updatedEvent.get("endTime").toString(), formatter);

            System.out.println("시간 파싱 완료: startTime=" + startTime + ", endTime=" + endTime);

            // OUT 데이터 생성
            Schedule originalSchedule = scheduleRepository.findById(originalScheduleId)
                    .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다. ID: " + originalScheduleId));
            WorkChange outChange = new WorkChange();
            outChange.setSchedule(originalSchedule);
            outChange.setChangeDate(LocalDate.parse(originalDate));
            outChange.setChangeStartTime(LocalDateTime.of(LocalDate.parse(originalDate), startTime.toLocalTime()));
            outChange.setChangeEndTime(LocalDateTime.of(LocalDate.parse(originalDate), endTime.toLocalTime()));
            outChange.setInOut("OUT");
            workChangeRepository.save(outChange);

            System.out.println("OUT 데이터 저장 완료: " + outChange);

            // IN 데이터 생성
            Schedule newSchedule = scheduleRepository.findById(newScheduleId)
                    .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다. ID: " + newScheduleId));
            WorkChange inChange = new WorkChange();
            inChange.setSchedule(newSchedule);
            inChange.setChangeDate(LocalDate.parse(newDate));
            inChange.setChangeStartTime(LocalDateTime.of(LocalDate.parse(newDate), startTime.toLocalTime()));
            inChange.setChangeEndTime(LocalDateTime.of(LocalDate.parse(newDate), endTime.toLocalTime()));
            inChange.setInOut("IN");
            workChangeRepository.save(inChange);

            System.out.println("IN 데이터 저장 완료: " + inChange);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "근무 변경이 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버에서 오류가 발생했습니다: " + e.getMessage());
        }
    }

}