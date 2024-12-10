package com.example.FinalProject.controller.employment;

import com.example.FinalProject.dto.ScheduleChangeDTO;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule saveSchedule = scheduleRepository.save(schedule);
        return ResponseEntity.ok(saveSchedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Integer id, @RequestBody Schedule scheduleDetails){
        return scheduleRepository.findById(id).map(schedule -> {
            schedule.setOfficialStart(scheduleDetails.getOfficialStart());
            schedule.setOfficialEnd(scheduleDetails.getOfficialEnd());
            schedule.setBreakMinute(scheduleDetails.getBreakMinute());
            schedule.setDay(scheduleDetails.getDay());
            Schedule updatedSchedule = scheduleRepository.save(schedule);
            return ResponseEntity.ok(updatedSchedule);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSchedule(@PathVariable Integer id) {
        return scheduleRepository.findById(id).map(schedule -> {
            scheduleRepository.delete(schedule);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
