package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
@Transactional
public class WorkChangeService {

    @Autowired
    private WorkChangeRepository workChangeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public void updateWorkChange(Integer originalScheduleId, LocalDate originalDate, Integer newScheduleId, LocalDate newDate) {
        // 기존 스케줄 처리
        Schedule originalSchedule = scheduleRepository.findById(originalScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("기존 스케줄이 존재하지 않습니다."));
        createWorkChange(originalSchedule, originalDate, "out");

        // 새로운 스케줄 처리
        Schedule newSchedule = scheduleRepository.findById(newScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("새로운 스케줄이 존재하지 않습니다."));
        createWorkChange(newSchedule, newDate, "in");
    }
    @Transactional
    private void createWorkChange(Schedule schedule, LocalDate date, String inOut) {
        WorkChange workChange = new WorkChange();
        workChange.setSchedule(schedule);
        workChange.setChangeDate(date);
        workChange.setChangeStartTime(date.atTime(schedule.getOfficialStart()));
        workChange.setChangeEndTime(date.atTime(schedule.getOfficialEnd()));
        workChange.setInOut(inOut);
        workChange.setPermit(true);
        workChange.setStatus("변경됨");

        workChangeRepository.save(workChange);
    }
}
