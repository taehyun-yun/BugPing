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

@Service
@Transactional
public class WorkChangeService {

    @Autowired
    private WorkChangeRepository workChangeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public void updateWorkChange(Integer originalScheduleId, LocalDate originalDate, Integer newScheduleId, LocalDate newDate) {
        // 1. 기존 날짜 OUT으로 처리
        handleWorkChange(originalScheduleId, originalDate, "OUT");

        // 2. 새로운 날짜 IN으로 처리
        handleWorkChange(newScheduleId, newDate, "IN");
    }

    private void handleWorkChange(Integer scheduleId, LocalDate date, String inOut) {
        // 스케줄 조회
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

        // 계약 기간 확인
        LocalDate contractStart = schedule.getContract().getContractStart().toLocalDate();
        LocalDate contractEnd = schedule.getContract().getContractEnd().toLocalDate();
        if (date.isBefore(contractStart) || date.isAfter(contractEnd)) {
            throw new IllegalArgumentException("변경된 날짜가 계약 기간을 벗어납니다.");
        }

        // 기존 데이터 조회 및 처리
        List<WorkChange> existingChanges = workChangeRepository.findBySchedule_ScheduleIdAndChangeDate(scheduleId, date);
        for (WorkChange change : existingChanges) {
            if (!change.getInOut().equals(inOut)) {
                workChangeRepository.delete(change);
            } else {
                change.setChangeStartTime(date.atTime(schedule.getOfficialStart()));
                change.setChangeEndTime(date.atTime(schedule.getOfficialEnd()));
                change.setStatus(inOut.equals("IN") ? "입력됨" : "출력됨");
                workChangeRepository.save(change);
                return;
            }
        }

        // 새로운 WorkChange 데이터 생성
        createNewWorkChange(scheduleId, date, inOut);
    }


    private void createNewWorkChange(Integer scheduleId, LocalDate date, String inOut) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));
        WorkChange newWorkChange = new WorkChange();
        newWorkChange.setSchedule(schedule);
        newWorkChange.setChangeDate(date);
        newWorkChange.setInOut(inOut);
        newWorkChange.setPermit(true);
        newWorkChange.setStatus(inOut.equals("IN") ? "입력됨" : "출력됨");
        newWorkChange.setChangeStartTime(date.atTime(schedule.getOfficialStart()));
        newWorkChange.setChangeEndTime(date.atTime(schedule.getOfficialEnd()));
        workChangeRepository.save(newWorkChange);
        System.out.println("새로운 WorkChange 생성 완료: " + newWorkChange);
    }
}
