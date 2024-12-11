package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // 공통 로직: Schedule -> Map 변환
    private Map<String, Object> convertScheduleToMap(Schedule schedule) {
        Map<String, Object> scheduleMap = new HashMap<>();
        LocalDate scheduleDate = LocalDate.now(); // 필요에 따라 스케줄 날짜 처리
        LocalDateTime officialStart = scheduleDate.atTime(schedule.getOfficialStart());
        LocalDateTime officialEnd = scheduleDate.atTime(schedule.getOfficialEnd());

        if (officialEnd.isBefore(officialStart)) {
            officialEnd = officialEnd.plusDays(1);
        }

        long totalMinutes = ChronoUnit.MINUTES.between(officialStart, officialEnd);
        int breakMinutes = schedule.getBreakMinute() != null ? schedule.getBreakMinute() : 0;

        if (totalMinutes >= 480) {
            breakMinutes = 60;
        } else if (totalMinutes >= 240) {
            breakMinutes = 30;
        }

        LocalDateTime finalEnd = officialEnd.minusMinutes(breakMinutes);

        scheduleMap.put("title", schedule.getContract().getWork().getUser().getName() + "님의 근무");
        scheduleMap.put("start", officialStart.toString());
        scheduleMap.put("end", finalEnd.toString());
        scheduleMap.put("description", "근무 시간 : " + totalMinutes / 60 + "시간" + totalMinutes % 60 + "분 (휴게: " + breakMinutes + "분)");
        scheduleMap.put("color", "#FFCC00");
        scheduleMap.put("status", "active");

        return scheduleMap;
    }

    // 전체 스케줄 데이터
    public List<Map<String, Object>> getUserSchedule(String userId) {
        List<Schedule> schedules = scheduleRepository.findByContract_Work_User_UserId(userId);
        List<Map<String, Object>> scheduleList = new ArrayList<>();
        for (Schedule schedule : schedules) {
            scheduleList.add(convertScheduleToMap(schedule));
        }
        return scheduleList;
    }

    // 페이징 처리된 스케줄 데이터
    public Page<Map<String, Object>> getPagedSchedulesByDateRange(LocalDate start, LocalDate end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Schedule> schedulesPage = scheduleRepository.findByOfficialStartBetween(start, end, pageable);
        return schedulesPage.map(this::convertScheduleToMap);
    }
}