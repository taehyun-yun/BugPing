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
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private WorkChangeRepository workChangeRepository;

    @Autowired
    private WorkChangeService workChangeService; // WorkChangeService 추가

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

        // 스케줄 ID 리스트 추출
        List<Integer> scheduleIds = schedules.stream()
                .map(Schedule::getScheduleId)
                .toList();

        // WorkChange 미리 조회
        List<WorkChange> workChanges = workChangeRepository.findAllByScheduleIdsAndDateRange(scheduleIds, start, end);

        // WorkChange를 Map으로 정리 (key: scheduleId+changeDate, value: WorkChange 리스트)
        Map<String, List<WorkChange>> workChangeMap = workChanges.stream()
                .collect(Collectors.groupingBy(wc -> wc.getSchedule().getScheduleId() + "_" + wc.getChangeDate()));

        for (Schedule schedule : schedules) {
            if (schedule.getContract() == null || schedule.getContract().getContractStart() == null ||
                    schedule.getContract().getContractEnd() == null) {
                continue; // 데이터가 누락된 스케줄은 무시
            }

            LocalDate contractStartDate = schedule.getContract().getContractStart().toLocalDate();
            LocalDate contractEndDate = schedule.getContract().getContractEnd().toLocalDate();

            // 현재 날짜가 Contract 범위에 포함되지 않으면 제외
            if (start != null && end != null) {
                if (contractEndDate.isBefore(start) || contractStartDate.isAfter(end)) {
                    System.out.println("Contract 범위에 포함되지 않음 - scheduleId: " + schedule.getScheduleId());
                    continue;
                }
            }

            LocalDate currentDate = contractStartDate;

            while (!currentDate.isAfter(contractEndDate)) {
                if ((start == null || !currentDate.isBefore(start)) &&
                        (end == null || !currentDate.isAfter(end))) {
                    System.out.println("날짜 확인 : " + currentDate);

                    // WorkChangeService를 사용해 변경된 WorkChange 조회
                    Optional<WorkChange> workChangeOptional = workChangeService.getLatestWorkChange(
                            schedule.getScheduleId(), currentDate
                    );

                    // WorkChange가 있는 경우 처리
                    if (workChangeOptional.isPresent()) {
                        WorkChange workChange = workChangeOptional.get();
                        String inOut = workChange.getInOut().trim().toUpperCase();

                        if ("OUT".equals(inOut)) {
                            System.out.println("OUT 상태로 제외: " + currentDate);
                            currentDate = currentDate.plusDays(1);
                            continue;
                        }

                        if ("IN".equals(inOut)) {
                            System.out.println("IN 상태 확인, 스케줄 추가 준비: " + currentDate);
                            Map<String, Object> scheduleMap = createScheduleMap(
                                    schedule, workChange.getChangeStartTime(), workChange.getChangeEndTime(), "변경된 근무 일정");
                            scheduleList.add(scheduleMap);
                            currentDate = currentDate.plusDays(1);
                            continue;
                        }
                    }

                    // WorkChange가 없으면 기존 스케줄 데이터를 사용
                    if (schedule.getDay() == currentDate.getDayOfWeek().getValue()) {
                        Map<String, Object> scheduleMap = createScheduleMap(schedule,
                                currentDate.atTime(schedule.getOfficialStart()),
                                currentDate.atTime(schedule.getOfficialEnd()),
                                "기존 근무 일정");
                        scheduleList.add(scheduleMap);
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return scheduleList;
    }

    // 스케줄 맵 생성 메서드
    private Map<String, Object> createScheduleMap(Schedule schedule, LocalDateTime start, LocalDateTime end, String description) {
        Map<String, Object> scheduleMap = new HashMap<>();
        scheduleMap.put("scheduleId", schedule.getScheduleId());
        scheduleMap.put("title", schedule.getContract().getWork().getUser().getName() + "님의 근무");
        scheduleMap.put("start", start.toString());
        scheduleMap.put("end", end.toString());
        scheduleMap.put("description", description);

        // 휴게시간 계산 로직
        long totalMinutes = ChronoUnit.MINUTES.between(start, end);
        long breakTime = 0;

        if (totalMinutes > 8 * 60) {
            breakTime = 60; // 8시간 초과
        } else if (totalMinutes > 4 * 60) {
            breakTime = 30; // 4시간 초과
        }

        long totalWorkMinutes = totalMinutes - breakTime; // 총 근무시간

        // 추가 데이터 삽입
        scheduleMap.put("breakTime", breakTime); // 휴게시간 (분)
        scheduleMap.put("totalWorkMinutes", totalWorkMinutes); // 총 근무시간 (분)

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
