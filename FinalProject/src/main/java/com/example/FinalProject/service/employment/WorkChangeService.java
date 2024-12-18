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

    public void changeWork(Integer scheduleId, WorkChange workChange) {
        // 스케줄 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));

        // 변경된 날짜와 시간을 설정
        LocalDateTime startDateTime = workChange.getChangeDate().atTime(schedule.getOfficialStart());
        LocalDateTime endDateTime = workChange.getChangeDate().atTime(schedule.getOfficialEnd());

        // 변경된 날짜와 시간 설정
        workChange.setSchedule(schedule);
        workChange.setChangeStartTime(startDateTime);
        workChange.setChangeEndTime(endDateTime);
        workChange.setPermit(true); // 기본적으로 허용된 상태
        workChange.setStatus("변경됨");
        
        // workChange 저장
        workChangeRepository.save(workChange);
    }
}