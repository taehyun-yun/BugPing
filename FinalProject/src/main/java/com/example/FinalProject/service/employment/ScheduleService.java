package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.employment.ContractRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.work.WorkRepository;
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
    private ContractRepository contractRepository;

    @Autowired
    private WorkRepository workRepository;


    // 특정 사용자 스케줄 조회 (기간 필터링 추가)
    public List<Map<String, Object>> getUserSchedule(String userId, LocalDate start, LocalDate end) {
        List<Schedule> schedules = scheduleRepository.findByContract_Work_User_UserId(userId);
        return generateScheduleList(schedules, start, end);
    }

    // 회사의 모든 사용자 스케줄 조회 (기간 필터링 추가)
    public List<Map<String, Object>> getCompanySchedule(Integer companyId, LocalDate start, LocalDate end) {
        // 회사 ID로 관련된 모든 사용자들의 스케줄 조회
        List<Work> works = workRepository.findByCompany_CompanyId(companyId); // 회사에 속한 모든 Work 조회
        List<Schedule> allSchedules = new ArrayList<>();

        // 특정 회사의 사용자들이 가진 근무 일정 전체를 가져옴
        for (Work work : works) {
            List<Contract> contracts = contractRepository.findByWork(work);
            for (Contract contract : contracts) {
                allSchedules.addAll(scheduleRepository.findByContract(contract));
            }
        }

        return generateScheduleList(allSchedules, start, end);
    }


    // 스케줄 데이터를 FullCalendar에 맞게 변환 (기간 필터링 추가)
    private List<Map<String, Object>> generateScheduleList(List<Schedule> schedules, LocalDate start, LocalDate end) {
        List<Map<String, Object>> scheduleList = new ArrayList<>();

        for (Schedule schedule : schedules) {
            // 계약 및 필드 값 검증
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
                        Map<String, Object> scheduleMap = new HashMap<>();
                        String userName = schedule.getContract().getWork().getUser().getName();
                        LocalDateTime officialStart = currentDate.atTime(schedule.getOfficialStart());
                        LocalDateTime officialEnd = currentDate.atTime(schedule.getOfficialEnd());

                        // 종료 시간이 시작 시간보다 빠를 경우 처리
                        if (officialEnd.isBefore(officialStart)) {
                            officialEnd = officialEnd.plusDays(1);
                        }

                        long totalMinutes = ChronoUnit.MINUTES.between(officialStart, officialEnd);
                        if (totalMinutes > 1440) {
                            continue; // 잘못된 데이터는 무시
                        }

                        int breakMinutes = schedule.getBreakMinute() != null ? schedule.getBreakMinute() : 0;
                        if (totalMinutes >= 480) {
                            breakMinutes = 60;
                        } else if (totalMinutes >= 240) {
                            breakMinutes = 30;
                        }

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
                currentDate = currentDate.plusDays(1);
            }
        }
        return scheduleList;
    }

    // userId로 work의 companyId 조회
    public Integer getCompanyIdByUserId(String userId) {
        Work work = workRepository.findByUser_UserId(userId); // userId로 Work 조회
        return work != null ? work.getCompany().getCompanyId() : null;
    }

    // 사용자 역할에 대한 스케줄 조회
    public List<Map<String, Object>> getSchedulesByRole(String userId, String role, LocalDate start, LocalDate end, boolean viewCompanySchedule) {
        if ("employer".equalsIgnoreCase(role) || viewCompanySchedule) {
            // employer 또는 viewCompanySchedule=true인 경우 회사 전체 일정 조회
            Integer companyId = getCompanyIdByUserId(userId); // 회사 ID 조회
            if (companyId != null) {
                return getCompanySchedule(companyId, start, end);
            } else {
                throw new IllegalStateException("회사 정보를 찾을 수 없습니다.");
            }
        } else {
            // 개인 일정 조회
            return getUserSchedule(userId, start, end);
        }
    }
}