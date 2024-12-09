package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private WorkChangeRepository WorkChangeRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }


    public List<WorkChange> getAllChanges() {
        return WorkChangeRepository.findAll();
    }
}
