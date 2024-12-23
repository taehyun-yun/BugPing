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
public class WorkChangeService {

    @Autowired
    private WorkChangeRepository workChangeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public void updateWorkChange(Integer scheduleId, String newStart, String newEnd) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄을 찾을 수 없습니다."));

        // 기존 WorkChange를 "OUT"으로 설정
        workChangeRepository.findFirstByScheduleAndInOutOrderByWorkChangeIdDesc(schedule, "IN")
                .ifPresent(existingWorkChange -> {
                    existingWorkChange.setInOut("OUT");
                    workChangeRepository.save(existingWorkChange);
                });

        // 새로운 WorkChange 생성
        WorkChange newWorkChange = new WorkChange();
        newWorkChange.setSchedule(schedule);
        newWorkChange.setChangeDate(LocalDate.now());
        newWorkChange.setChangeStartTime(LocalDateTime.parse(newStart));
        newWorkChange.setChangeEndTime(newEnd != null ? LocalDateTime.parse(newEnd) : null);
        newWorkChange.setInOut("IN");
        workChangeRepository.save(newWorkChange);
    }
}