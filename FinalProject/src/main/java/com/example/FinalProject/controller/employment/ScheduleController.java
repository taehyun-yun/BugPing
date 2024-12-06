package com.example.FinalProject.controller.employment;

import com.example.FinalProject.dto.ScheduleAndChangeDTO;
import com.example.FinalProject.entity.employment.Change;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ChangeRepository;
import com.example.FinalProject.service.employment.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ChangeRepository changeRepository;


    @GetMapping("/scheduleRequests")
    public ScheduleAndChangeDTO getSchedulesAndChanges() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        List<Change> changeRequests = scheduleService.getAllChanges();

        return new ScheduleAndChangeDTO(schedules, changeRequests);
    }
}
