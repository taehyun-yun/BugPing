package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WorkChangeRepository changeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PostMapping("/api/schedules")
    public ResponseEntity<Schedule> saveSchedule(@RequestBody Schedule schedule) {
        System.out.println("받은 데이터: " + schedule); // 요청 데이터 확인
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return ResponseEntity.ok(savedSchedule);
    }

}
