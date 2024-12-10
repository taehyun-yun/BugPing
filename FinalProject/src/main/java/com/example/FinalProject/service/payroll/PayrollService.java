package com.example.FinalProject.service.payroll;

import com.example.FinalProject.dto.AttendanceDTO;
import com.example.FinalProject.dto.ContractDTO;
import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.repository.payroll.EmployeeRepository;
import com.example.FinalProject.repository.payroll.PayrollRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class PayrollService {

//    @Autowired
//    private PayrollRepository payrollRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    // 급여 계산 메서드
    public PayrollResponseDTO calculatePayroll(PayrollRequestDTO requestDTO) {
        // Mock 데이터를 가져옴
        List<AttendanceDTO> attendanceList = getMockAttendanceData(
                requestDTO.getEmployeeId(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate()
        );

        ContractDTO contractDTO = getMockContractData(requestDTO.getEmployeeId());

        // 항목별 초기화
        double basicSalary = 0; // 기본급
        double weeklyAllowance = 0; // 주휴수당
        double overtimePay = 0; // 연장수당
        double nightPay = 0; // 야간수당
        double deduction = 0; // 공제액

        // 근무시간 기반 급여 계산
        for (AttendanceDTO attendanceDTO : attendanceList) {
            int regularMinutes = attendanceDTO.getRecognizedWorkMinute(); // 정규 근무 시간 (분)
            int overtimeMinutes = attendanceDTO.getOvertimeMinute(); // 초과 근무 시간 (분)
            int nightMinutes = calculateNightMinutes(attendanceDTO); // 야간 근무 시간 (분)

            // 기본급, 초과 근무수당, 야간수당 계산
            basicSalary += (regularMinutes / 60.0) * contractDTO.getHourlyWage(); // 기본급
            overtimePay += (overtimeMinutes / 60.0) * contractDTO.getHourlyWage() * 1.5; // 초과 근무 1.5배
            nightPay += (nightMinutes / 60.0) * contractDTO.getHourlyWage() * 0.5; // 야간 할증

            log.info("기본급 계산 - 분: {}, 기본급: {}", regularMinutes, (regularMinutes / 60.0) * contractDTO.getHourlyWage());
            log.info("야간 수당 계산 - 분: {}, 야간수당: {}", nightMinutes, (nightMinutes / 60.0) * contractDTO.getHourlyWage() * 1.5);
        }

        // 주휴수당 계산
        weeklyAllowance = calculateWeeklyAllowance(attendanceList, contractDTO.getHourlyWage());

        // 공제액 (일단 세금 10%)
        deduction = (basicSalary + overtimePay + nightPay + weeklyAllowance) * 0.1;

        // 총 급여 계산
        double totalSalary = basicSalary + overtimePay + nightPay + weeklyAllowance - deduction;

        log.info("급여 계산 완료 - 직원ID: {}, 기본급: {}, 주휴수당: {}, 연장수당: {}, 야간수당: {}, 실수령액: {}",
                requestDTO.getEmployeeId(), basicSalary, weeklyAllowance, overtimePay, nightPay, totalSalary);

        // 응답 DTO 생성
        return new PayrollResponseDTO(
                requestDTO.getEmployeeId(),
                "윤태현",  // 임시 이름
                basicSalary,
                weeklyAllowance,
                overtimePay,
                nightPay,
                deduction,
                totalSalary
        );
    } // end calculatePayroll
    
    // 총 근무 시간 계산
    private double calculateTotalWorkHours(List<AttendanceDTO> attendanceList) {
        double totalMinutes = 0;
        for (AttendanceDTO attendanceDTO : attendanceList) {
            totalMinutes += attendanceDTO.getRecognizedWorkMinute();
        }
        return totalMinutes / 60.0; // 시간 단위로 반환
    }// end calculateTotalWorkHours

    // 주휴수당 계산 메서드
    private double calculateWeeklyAllowance(List<AttendanceDTO> attendanceList, double hourlyWage) {

        // 4주 평균 근무 시간 계산
        double avgWeeklyHours = calculateTotalWorkHours(attendanceList) / 4.0;
        log.info("평균 주 근무 시간: {}", avgWeeklyHours);

        // 개근 여부 확인
        boolean isFullAttended = true; // 초기값은 true로 설정
        for (AttendanceDTO attendanceDTO : attendanceList) {
            if (!"정상".equals(attendanceDTO.getIsNormalAttendance())) {
                isFullAttended = false;
                break; // 한 번이라도 정상 출근이 아닌 경우 반복문 종료
            }
        }
        log.info("개근 여부: {}", isFullAttended);
        // 주휴수당 계산
        if (avgWeeklyHours >= 15 && isFullAttended) {
            log.info("주휴수당 조건 만족 - 시급: {}, 주휴수당: {}", hourlyWage, hourlyWage * 8);
            return hourlyWage * 8; // 주휴수당 : 하루 8시간 기준
        } else {
            log.warn("주휴수당 조건 미충족 - 평균 주 근무 시간: {}, 개근 여부: {}", avgWeeklyHours, isFullAttended);
            return 0; // 조건 미충족
        }
    }

    // 야간 수당 메서드
    private int calculateNightMinutes(AttendanceDTO attendanceDTO) {
        LocalDateTime startDateTime = attendanceDTO.getActualStart();
        LocalDateTime endDateTime = attendanceDTO.getActualEnd();

        // 야간 근무 시간 기준 (22시 ~ 06시)
        LocalDateTime nightStart = startDateTime.toLocalDate().atTime(22, 0); // 당일 22:00
        LocalDateTime nightEnd = startDateTime.toLocalDate().plusDays(1).atTime(6, 0); // 다음 날 06:00

        int nightMinutes = 0;

        // 근무시간이 야간 시간(22:00 - 06:00)과 겹치는지 확인
        if (startDateTime.isBefore(nightEnd) && endDateTime.isAfter(nightStart)) {
            // 겹치는 야간 시간 계산
            LocalDateTime effectiveStart = startDateTime.isAfter(nightStart) ? startDateTime : nightStart;
            LocalDateTime effectiveEnd = endDateTime.isBefore(nightEnd) ? endDateTime : nightEnd;
            nightMinutes += (int) Duration.between(effectiveStart, effectiveEnd).toMinutes();
        }

        log.info("야간 근무 계산 - 시작: {}, 종료: {}, 야간분: {}", startDateTime, endDateTime, nightMinutes);

        return Math.max(nightMinutes, 0); // 음수 방지
    } // end calculateNightHours

    // Mock 데이터 가져오기 (Attendance와 Contract)
    private List<AttendanceDTO> getMockAttendanceData(Integer employeeId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceDTO> allData = List.of(
                // 1주차 근무
                new AttendanceDTO(1, employeeId, LocalDateTime.of(2023, 12, 1, 9, 0), LocalDateTime.of(2023, 12, 1, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480),
                new AttendanceDTO(2, employeeId, LocalDateTime.of(2023, 12, 2, 9, 0), LocalDateTime.of(2023, 12, 2, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480),
                // 2주차 근무
                new AttendanceDTO(3, employeeId, LocalDateTime.of(2023, 12, 8, 9, 0), LocalDateTime.of(2023, 12, 8, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480),
                new AttendanceDTO(4, employeeId, LocalDateTime.of(2023, 12, 9, 9, 0), LocalDateTime.of(2023, 12, 9, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480),
                // 3주차 근무
                new AttendanceDTO(5, employeeId, LocalDateTime.of(2023, 12, 15, 9, 0), LocalDateTime.of(2023, 12, 15, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480),
                new AttendanceDTO(6, employeeId, LocalDateTime.of(2023, 12, 16, 9, 0), LocalDateTime.of(2023, 12, 16, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480),
                // 4주차 근무
                new AttendanceDTO(7, employeeId, LocalDateTime.of(2023, 12, 22, 9, 0), LocalDateTime.of(2023, 12, 22, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480),
                new AttendanceDTO(8, employeeId, LocalDateTime.of(2023, 12, 23, 9, 0), LocalDateTime.of(2023, 12, 23, 18, 0),
                        "정상", "특이사항 없음", "정상", 480, "없음", 0, 480)
        );

        //startDate와 endDate를 기준으로 데이터를 필터링.
        return allData.stream()
                .filter(data -> data.getActualStart() != null &&
                        !data.getActualStart().toLocalDate().isBefore(startDate) &&
                        !data.getActualEnd().toLocalDate().isAfter(endDate))
                .toList();
    }

    private ContractDTO getMockContractData(Integer employeeId) {
        // Mock Contract 데이터 생성
        return new ContractDTO(1, "KOSA", 9860, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
    }

    public List<EmployeeDTO> getEmployeeListWithPayroll() {
        List<EmployeeDTO> employeeList = new ArrayList<>();

        // Mock 데이터 추가
        employeeList.add(new EmployeeDTO(1, "윤태현", "2024/10/03", "12,000", 60, 12, 907599.0, false));
        employeeList.add(new EmployeeDTO(2, "유지훈", "2024/10/03", "12,000", 60, 12, 907599.0, false));
        employeeList.add(new EmployeeDTO(3, "장종현", "2024/10/03", "12,000", 60, 12, 907599.0, false));
        employeeList.add(new EmployeeDTO(4, "문준호", "2024/10/03", "12,000", 60, 12, 907599.0, true));

        return employeeList;
    }

//    // 페이징
//    public Page<EmployeeDTO> getEmployees(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
//        return employeeRepository.findAll(pageable);
//    }

}// end service