package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkChangeService {

    @Autowired
    private WorkChangeRepository workChangeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public Optional<WorkChange> getLatestWorkChange(Integer scheduleId, LocalDate changeDate) {
        return workChangeRepository.findLatestWorkChange(scheduleId, changeDate);
    }

    public void updateWorkChange(Integer originalScheduleId, LocalDate originalDate, Integer newScheduleId, LocalDate newDate) {
        // 기존 스케줄 처리
        Schedule originalSchedule = scheduleRepository.findById(originalScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("기존 스케줄이 존재하지 않습니다."));
        handleWorkChange(originalSchedule, originalDate, "OUT");

        // 새로운 스케줄 처리
        Schedule newSchedule = scheduleRepository.findById(newScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("새로운 스케줄이 존재하지 않습니다."));
        handleWorkChange(newSchedule, newDate, "IN");
    }

    @Transactional
    private void handleWorkChange(Schedule schedule, LocalDate date, String inOut) {
        // 기존 WorkChange 확인
        Optional<WorkChange> existingChange = workChangeRepository.findBySchedule_ScheduleIdAndChangeDateAndInOut(
                schedule.getScheduleId(), date, inOut);

        if (existingChange.isPresent()) {
            // 기존 데이터 업데이트
            WorkChange workChange = existingChange.get();
            workChange.setChangeStartTime(date.atTime(schedule.getOfficialStart()));
            workChange.setChangeEndTime(date.atTime(schedule.getOfficialEnd()));
            workChangeRepository.save(workChange);
            System.out.println("기존 WorkChange 업데이트: " + workChange);
        } else {
            // 새로운 데이터 생성
            WorkChange newWorkChange = new WorkChange();
            newWorkChange.setSchedule(schedule);
            newWorkChange.setChangeDate(date);
            newWorkChange.setChangeStartTime(date.atTime(schedule.getOfficialStart()));
            newWorkChange.setChangeEndTime(date.atTime(schedule.getOfficialEnd()));
            newWorkChange.setInOut(inOut);
            newWorkChange.setPermit(true);
            newWorkChange.setStatus("변경됨");
            workChangeRepository.save(newWorkChange);
            System.out.println("새로운 WorkChange 추가: " + newWorkChange);
        }

        // IN과 OUT 관계 검증
        if ("OUT".equals(inOut)) {
            boolean hasIn = workChangeRepository.findBySchedule_ScheduleIdAndChangeDateAndInOut(
                    schedule.getScheduleId(), date, "IN").isPresent();
            if (!hasIn) {
                throw new IllegalStateException("OUT 데이터를 추가하려면 IN 데이터가 필요합니다.");
            }
        } else if ("IN".equals(inOut)) {
            boolean hasOut = workChangeRepository.findBySchedule_ScheduleIdAndChangeDateAndInOut(
                    schedule.getScheduleId(), date, "OUT").isPresent();
            if (!hasOut) {
                throw new IllegalStateException("IN 데이터를 추가하려면 OUT 데이터가 필요합니다.");
            }
        }
    }
}
