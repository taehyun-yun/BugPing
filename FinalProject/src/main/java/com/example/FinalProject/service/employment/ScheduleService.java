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

                        // WorkChange에서 변경된 데이터 확인
                        WorkChange workChange = workChangeRepository.findLatestWorkChange(
                                schedule.getScheduleId(), currentDate);

                        LocalDateTime officialStart;
                        LocalDateTime officialEnd;
                        String description = "기존 근무 일정";

                        if (workChange != null) {
                            if ("out".equals(workChange.getInOut())) {
                                // 해당 날짜의 스케줄을 제외
                                currentDate = currentDate.plusDays(1);
                                continue;
                            } else if ("in".equals(workChange.getInOut())) {
                                // 변경된 시간 사용
                                officialStart = workChange.getChangeStartTime();
                                officialEnd = workChange.getChangeEndTime();
                                description = "변경된 근무 일정";
                            } else {
                                // inOut 값이 없거나 다른 값인 경우 기본 일정 사용
                                officialStart = currentDate.atTime(schedule.getOfficialStart());
                                officialEnd = currentDate.atTime(schedule.getOfficialEnd());
                            }
                        } else {
                            // 변경 사항이 없으면 기존 데이터 사용
                            officialStart = currentDate.atTime(schedule.getOfficialStart());
                            officialEnd = currentDate.atTime(schedule.getOfficialEnd());
                        }

                        // 종료 시간이 시작 시간보다 빠를 경우 처리
                        if (officialEnd.isBefore(officialStart)) {
                            officialEnd = officialEnd.plusDays(1);
                        }

                        // 근무 시간 계산이 하루가 넘어갈 경우 방지
                        long totalMinutes = ChronoUnit.MINUTES.between(officialStart, officialEnd);
                        if (totalMinutes > 1440) {
                            currentDate = currentDate.plusDays(1);
                            continue;
                        }

                        // 휴게시간 계산
                        int breakMinutes = 0;
                        LocalDateTime finalEnd = officialEnd;

                        if (totalMinutes >= 480) { // 8시간 이상
                            breakMinutes = 60;
                            finalEnd = officialEnd.minusMinutes(breakMinutes);
                        } else if (totalMinutes >= 240) { // 4시간 이상
                            breakMinutes = 30;
                            finalEnd = officialEnd.minusMinutes(breakMinutes);
                        }

                        // FullCalendar용 데이터 생성
                        Map<String, Object> scheduleMap = new HashMap<>();
                        String userName = schedule.getContract().getWork().getUser().getName();

                        scheduleMap.put("scheduleId", schedule.getScheduleId());
                        scheduleMap.put("title", userName + "님의 근무");
                        scheduleMap.put("start", officialStart.toString());
                        scheduleMap.put("end", finalEnd.toString());
                        scheduleMap.put("description", description + " (근무 시간: " + totalMinutes / 60 + "시간 " +
                                totalMinutes % 60 + "분, 휴게: " + breakMinutes + "분)");

                        scheduleList.add(scheduleMap);
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }
        return scheduleList;
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
