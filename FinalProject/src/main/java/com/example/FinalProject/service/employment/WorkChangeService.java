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
        // 기존 데이터 조회
        List<WorkChange> existingChanges = workChangeRepository.findBySchedule_ScheduleIdAndChangeDate(scheduleId, date);

        // 기존 데이터 삭제 또는 상태 업데이트
        for (WorkChange change : existingChanges) {
            if (!change.getInOut().equals(inOut)) {
                // IN/OUT 상태가 다르면 삭제
                workChangeRepository.delete(change);
                System.out.println("삭제된 WorkChange 데이터: " + change);
            } else {
                // 동일한 상태(IN/OUT)이면 업데이트
                change.setChangeStartTime(date.atTime(change.getSchedule().getOfficialStart()));
                change.setChangeEndTime(date.atTime(change.getSchedule().getOfficialEnd()));
                change.setStatus(inOut.equals("IN") ? "입력됨" : "출력됨");
                workChangeRepository.save(change);
                System.out.println("업데이트된 WorkChange 데이터: " + change);
                return; // 동일한 데이터가 있으면 종료
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
