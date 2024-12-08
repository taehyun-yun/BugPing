package com.example.FinalProject.controller.employment;

import com.example.FinalProject.dto.ScheduleAndChangeDTO;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WorkChangeRepository changeRepository;

    @PostMapping("/schedules")
    /*public Schedule createSchedule(@RequestBody Schedule schedule){
        return scheduleService.createSchedule(schedule);
    }*/
    public ResponseEntity<Schedule> saveSchedule(@RequestBody Schedule schedule) {
        // 로직 처리 후 Schedule 객체 반환
        return ResponseEntity.ok(schedule);
    }
}
