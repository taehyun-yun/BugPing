package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ContractRepository1;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ContractRepository1 contractRepository1;

    // 특정 사용자 스케줄 조회 (기간 필터링 추가)
    public List<Map<String, Object>> getUserSchedule(String userId, LocalDate start, LocalDate end) {
        List<Schedule> schedules = scheduleRepository.findByContract_Work_User_UserId(userId);
        return generateScheduleList(schedules, start, end);
    }

    // 회사의 모든 사용자 스케줄 조회 (기간 필터링 추가)
    public List<Map<String, Object>> getCompanySchedule(String companyId, LocalDate start, LocalDate end) {
        List<Contract> contracts = contractRepository1.findByCompanyId(companyId);
        List<Schedule> allSchedules = new ArrayList<>();

        // 계약별로 모든 스케줄 조회
        for (Contract contract : contracts) {
            allSchedules.addAll(scheduleRepository.findByContract(contract));
        }

        return generateScheduleList(allSchedules, start, end);
    }

    // 스케줄 데이터를 FullCalendar에 맞게 변환 (기간 필터링 추가)
    private List<Map<String, Object>> generateScheduleList(List<Schedule> schedules, LocalDate start, LocalDate end) {
        List<Map<String, Object>> scheduleList = new ArrayList<>();

        for (Schedule schedule : schedules) {
            LocalDate contractStartDate = schedule.getContract().getContractStart().toLocalDate();
            LocalDate contractEndDate = schedule.getContract().getContractEnd().toLocalDate();

            // 계약 기간 동안의 모든 날짜를 반복
            LocalDate currentDate = contractStartDate;
            while (!currentDate.isAfter(contractEndDate)) {
                // 기간 필터링 추가
                if ((start == null || !currentDate.isBefore(start)) &&
                        (end == null || !currentDate.isAfter(end))) {

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
                            throw new IllegalArgumentException("근무 시간이 24시간을 초과할 수 없습니다.");
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
                }
                currentDate = currentDate.plusDays(1); // 날짜 증가
            }
        }
        return scheduleList;
    }
}
