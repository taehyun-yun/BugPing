package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private WorkChangeRepository workChangeRepository;

    // 특정 사용자 스케줄 조회 (기간 필터링 추가)
    public List<Map<String, Object>> getUserSchedule(String userId, LocalDate start, LocalDate end) {
        List<Schedule> schedules = scheduleRepository.findByContract_Work_User_UserId(userId);
        return generateScheduleList(schedules, start, end);
    }

    // 회사의 모든 사용자 스케줄 조회 (기간 필터링 추가)
    public List<Map<String, Object>> getCompanySchedule(Integer companyId, LocalDate start, LocalDate end) {
        List<Schedule> companySchedule = scheduleRepository.findByContract_Work_Company_CompanyId(companyId);
        return generateScheduleList(companySchedule, start, end);
    }

    // 스케줄 데이터를 FullCalendar에 맞게 변환 (WorkChange 반영 추가)
    private List<Map<String, Object>> generateScheduleList(List<Schedule> schedules, LocalDate start, LocalDate end) {
        List<Map<String, Object>> scheduleList = new ArrayList<>();

        for (Schedule schedule : schedules) {
            if (schedule.getContract() == null || schedule.getContract().getContractStart() == null ||
                    schedule.getContract().getContractEnd() == null) {
                continue; // 데이터가 누락된 스케줄은 무시
            }

            LocalDate contractStartDate = schedule.getContract().getContractStart().toLocalDate();
            LocalDate contractEndDate = schedule.getContract().getContractEnd().toLocalDate();
            LocalDate currentDate = contractStartDate;

            while (!currentDate.isAfter(contractEndDate)) {
                if ((start == null || !currentDate.isBefore(start)) &&
                        (end == null || !currentDate.isAfter(end))) {

                    if (schedule.getDay() == currentDate.getDayOfWeek().getValue()) {
                        // WorkChange 데이터 조회
                        Optional<WorkChange> workChange = workChangeRepository
                                .findFirstByScheduleAndInOutOrderByWorkChangeIdDesc(schedule, "IN");

                        // WorkChange 데이터가 존재하면 해당 데이터를 적용
                        LocalDateTime workStart = workChange.map(WorkChange::getChangeStartTime)
                                .orElse(currentDate.atTime(schedule.getOfficialStart()));
                        LocalDateTime workEnd = workChange.map(WorkChange::getChangeEndTime)
                                .orElse(currentDate.atTime(schedule.getOfficialEnd()));

                        // 근무 시간 계산 (분 단위)
                        long workDuration = ChronoUnit.MINUTES.between(workStart, workEnd);

                        // 휴게 시간 계산
                        long breakTime = calculateBreakTime(workDuration);

                        // 스케줄 데이터 생성
                        Map<String, Object> scheduleMap = createScheduleMap(schedule, workStart, workEnd,
                                workChange.isPresent() ? "변경된 근무 일정" : "기존 근무 일정");

                        // 근무 시간 및 휴게 시간 추가
                        scheduleMap.put("workDuration", workDuration - breakTime); // 총 근무 시간 (분)
                        scheduleMap.put("breakTime", breakTime);       // 휴게 시간 (분)

                        scheduleList.add(scheduleMap);
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return scheduleList;
    }

    // 휴게 시간을 계산하는 메서드
    private long calculateBreakTime(long workDurationMinutes) {
        if (workDurationMinutes > 480) { // 8시간 초과
            return 60; // 휴게 시간: 60분
        } else if (workDurationMinutes > 240) { // 4시간 초과
            return 30; // 휴게 시간: 30분
        } else { // 4시간 이하
            return 0; // 휴게 시간 없음
        }
    }

    // 스케줄 맵 생성 메서드
    private Map<String, Object> createScheduleMap(Schedule schedule, LocalDateTime start, LocalDateTime end, String description) {
        Map<String, Object> scheduleMap = new HashMap<>();
        scheduleMap.put("scheduleId", schedule.getScheduleId());
        scheduleMap.put("title", schedule.getContract().getWork().getUser().getName() + "님의 근무");
        scheduleMap.put("start", start.toString());
        scheduleMap.put("end", end.toString());
        scheduleMap.put("description", description);
        return scheduleMap;
    }


    // userId로 work의 companyId 조회
    public Integer getCompanyIdByUserId(String userId) {
        Work work = workRepository.findByUser_UserId(userId);
        return work != null ? work.getCompany().getCompanyId() : null;
    }

    // 사용자 역할에 대한 스케줄 조회
    public List<Map<String, Object>> getSchedulesByRole(String userId, String role, LocalDate start, LocalDate end, boolean viewCompanySchedule) {
        if ("employer".equalsIgnoreCase(role) || viewCompanySchedule) {
            Integer companyId = getCompanyIdByUserId(userId);
            if (companyId != null) {
                return getCompanySchedule(companyId, start, end);
            } else {
                throw new IllegalStateException("회사 정보를 찾을 수 없습니다.");
            }
        } else {
            return getUserSchedule(userId, start, end);
        }
    }
}
