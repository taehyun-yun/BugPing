package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Map<String, Object>> getUserSchedule(String userId) {
        List<Schedule> schedules = scheduleRepository.findByContract_Work_User_UserId(userId);
        List<Map<String, Object>> scheduleList = new ArrayList<>();

        for (Schedule schedule : schedules) {
            LocalDate contractStartDate = schedule.getContract().getContractStart().toLocalDate();
            LocalDate contractEndDate = schedule.getContract().getContractEnd().toLocalDate();

            // 계약 기간 동안의 모든 날짜를 반복
            LocalDate currentDate = contractStartDate;
            while (!currentDate.isAfter(contractEndDate)) {
                // 스케줄의 요일과 현재 날짜의 요일 비교
                if (schedule.getDay() == currentDate.getDayOfWeek().getValue()) {
                    Map<String, Object> scheduleMap = new HashMap<>();

                    // 사용자 이름 가져오기
                    String userName = schedule.getContract().getWork().getUser().getName();

                    // 근무 시작/종료 시간 계산
                    LocalDateTime officialStart = currentDate.atTime(schedule.getOfficialStart());
                    LocalDateTime officialEnd = currentDate.atTime(schedule.getOfficialEnd());

                    // 종료 시간이 다음 날로 넘어가는 경우 처리
                    if (officialEnd.isBefore(officialStart)) {
                        officialEnd = officialEnd.plusDays(1);
                    }

                    // 총 근무 시간 계산
                    long totalMinutes = ChronoUnit.MINUTES.between(officialStart, officialEnd);
                    if (totalMinutes > 1440) {
                        throw new IllegalArgumentException("근무 시간이 24시간을 초과 할 수 없습니다.");
                    }

                    // 휴게 시간 계산
                    int breakMinutes = schedule.getBreakMinute() != null ? schedule.getBreakMinute() : 0;
                    if (totalMinutes >= 480) {
                        breakMinutes = 60;
                    } else if (totalMinutes >= 240) {
                        breakMinutes = 30;
                    }

                    // 휴게 시간을 반영한 종료 시간
                    LocalDateTime finalEnd = officialEnd.minusMinutes(breakMinutes);

                    // FullCalendar 데이터 구성
                    scheduleMap.put("title", userName + "님의 근무");
                    scheduleMap.put("start", officialStart.toString());
                    scheduleMap.put("end", finalEnd.toString());
                    scheduleMap.put("description", "근무 시간 : " + totalMinutes / 60 + "시간 " + totalMinutes % 60 + "분 (휴게: " + breakMinutes + "분)");
                    scheduleMap.put("color", "#FFCC00");
                    scheduleMap.put("status", "active");

                    scheduleList.add(scheduleMap);
                }
                currentDate = currentDate.plusDays(1); // 날짜 증가
            }
        }
        return scheduleList;
    }
}
