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
    private WorkChangeService workChangeService;

    // userId로 사용자가 속한 모든 companyId 조회 (단일/복수 모두 처리)
    public List<Integer> getCompanyIdsByUserId(String userId) {
        List<Work> userWorks = workRepository.findByUser_UserId(userId);
        return userWorks.stream()
                .map(work -> work.getCompany().getCompanyId())
                .distinct()
                .collect(Collectors.toList());
    }

    // 특정 사용자 스케줄 조회
    public List<Map<String, Object>> getUserSchedule(String userId, LocalDate start, LocalDate end) {
        List<Schedule> schedules = scheduleRepository.findByContract_Work_User_UserId(userId);
        return generateScheduleList(schedules, start, end);
    }

    // 회사의 모든 사용자 스케줄 조회
    public List<Map<String, Object>> getCompanySchedule(Integer companyId, LocalDate start, LocalDate end) {
        List<Schedule> companySchedule = scheduleRepository.findByContract_Work_Company_CompanyId(companyId);
        return generateScheduleList(companySchedule, start, end);
    }

    // 스케줄 데이터를 FullCalendar에 맞게 변환
    private List<Map<String, Object>> generateScheduleList(List<Schedule> schedules, LocalDate start, LocalDate end) {
        List<Map<String, Object>> scheduleList = new ArrayList<>();
        List<Integer> scheduleIds = schedules.stream().map(Schedule::getScheduleId).toList();

        // WorkChange 데이터 로드
        List<WorkChange> workChanges = workChangeRepository.findAllByScheduleIdsAndDateRange(scheduleIds, start, end);
        Map<String, List<WorkChange>> workChangeMap = workChanges.stream()
                .collect(Collectors.groupingBy(wc -> wc.getSchedule().getScheduleId() + "_" + wc.getChangeDate()));

        for (Schedule schedule : schedules) {
            LocalDate contractStart = schedule.getContract().getContractStart().toLocalDate();
            LocalDate contractEnd = schedule.getContract().getContractEnd().toLocalDate();
            LocalDate currentDate = contractStart;

            while (!currentDate.isAfter(contractEnd)) {
                if ((start == null || !currentDate.isBefore(start)) &&
                        (end == null || !currentDate.isAfter(end))) {

                    // WorkChange 데이터 확인
                    String key = schedule.getScheduleId() + "_" + currentDate;
                    List<WorkChange> changes = workChangeMap.get(key);

                    if (changes != null && !changes.isEmpty()) {
                        for (WorkChange change : changes) {
                            if ("OUT".equals(change.getInOut())) {
                                System.out.println("OUT 상태, 표시하지 않음: " + currentDate);
                                continue;
                            } else if ("IN".equals(change.getInOut())) {
                                scheduleList.add(createScheduleMap(schedule, change.getChangeStartTime(),
                                        change.getChangeEndTime(), "변경된 근무 일정"));
                                continue;
                            }
                        }
                    } else if (schedule.getDay() == currentDate.getDayOfWeek().getValue()) {
                        // 기본 스케줄 추가
                        scheduleList.add(createScheduleMap(schedule,
                                currentDate.atTime(schedule.getOfficialStart()),
                                currentDate.atTime(schedule.getOfficialEnd()),
                                "기존 근무 일정"));
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return scheduleList;
    }

    // 스케줄 맵 생성
    private Map<String, Object> createScheduleMap(Schedule schedule, LocalDateTime start, LocalDateTime end, String description) {
        Map<String, Object> scheduleMap = new HashMap<>();
        scheduleMap.put("scheduleId", schedule.getScheduleId());
        scheduleMap.put("title", schedule.getContract().getWork().getUser().getName() + "님의 근무");
        scheduleMap.put("start", start.toString());
        scheduleMap.put("end", end.toString());
        scheduleMap.put("description", description);

        long totalMinutes = ChronoUnit.MINUTES.between(start, end);
        long breakTime = totalMinutes > 8 * 60 ? 60 : totalMinutes > 4 * 60 ? 30 : 0;

        scheduleMap.put("breakTime", breakTime);
        scheduleMap.put("totalWorkMinutes", totalMinutes - breakTime);

        return scheduleMap;
    }

    // 사용자 역할에 대한 스케줄 조회
    public List<Map<String, Object>> getSchedulesByRole(
            String userId,
            String role,
            LocalDate start,
            LocalDate end,
            boolean viewCompanySchedule,
            Integer selectedCompanyId
    ) {
        // 사용자가 속한 모든 회사 ID 조회
        List<Integer> companyIds = getCompanyIdsByUserId(userId);

        // 1. 특정 회사 ID로 스케줄 조회
        if (selectedCompanyId != null) {
            if (companyIds.contains(selectedCompanyId)) {
                // 선택된 회사 ID가 사용자 회사 목록에 포함된 경우
                return getCompanySchedule(selectedCompanyId, start, end);
            } else {
                throw new IllegalArgumentException("사용자가 속하지 않은 회사입니다. 회사 ID: " + selectedCompanyId);
            }
        }

        // 2. 회사 전체 스케줄 조회 (고용주이거나 회사 스케줄 보기가 활성화된 경우)
        if ("employer".equalsIgnoreCase(role) || viewCompanySchedule) {
            if (!companyIds.isEmpty()) {
                // 사용자의 첫 번째 회사 ID로 조회
                return getCompanySchedule(companyIds.get(0), start, end);
            } else {
                throw new IllegalStateException("사용자가 속한 회사가 없습니다.");
            }
        }

        // 3. 사용자 개인 스케줄 조회
        return getUserSchedule(userId, start, end);
    }
}
