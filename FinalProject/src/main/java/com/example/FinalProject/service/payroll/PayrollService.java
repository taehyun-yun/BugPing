package com.example.FinalProject.service.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.payroll.PayRoll;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.contract.ContractRepository;
import com.example.FinalProject.repository.payroll.PayrollRepository;
import com.example.FinalProject.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PayrollService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    // 급여 계산 메서드
    public PayrollResponseDTO calculatePayroll(PayrollRequestDTO requestDTO) {
        log.info("급여 계산 요청 - User ID: {}, Start Date: {}, End Date: {}",
                requestDTO.getUserId(), requestDTO.getStartDate(), requestDTO.getEndDate());

        // 계약 정보 조회 // 근데 workId는 왜 null 로 주는거지?
        Contract contract = findContractByWorkOrUser(requestDTO.getUserId(), null, requestDTO.getStartDate(), requestDTO.getEndDate());
        if (contract == null) {
            throw new IllegalArgumentException("유효한 계약 정보를 찾을 수 없습니다.");
        }

        // 출근 데이터 조회
        List<Attendance> attendanceList = findAttendanceData(requestDTO.getUserId(), null, requestDTO.getStartDate(), requestDTO.getEndDate());
        if (attendanceList.isEmpty()) {
            throw new IllegalArgumentException("출근 데이터가 없습니다.");
        }

        // 급여 관련 계산
        double basicSalary = calculateBasicSalary(attendanceList, contract);
        double weeklyAllowance = calculateWeeklyAllowance(attendanceList, contract.getHourlyWage());
        double nightPay = calculateNightPay(attendanceList, contract.getHourlyWage());
        double overtimePay = calculateOvertimePay(attendanceList, contract.getHourlyWage());
        double deduction = (basicSalary + weeklyAllowance + nightPay) * 0.1;
        double totalSalary = basicSalary + weeklyAllowance + nightPay - deduction;

        log.info("급여 계산 완료 - Basic Salary: {}, Weekly Allowance: {}, Night Pay: {}, Overtime Pay: {}, Deduction: {}, Total Salary: {}",
                basicSalary, weeklyAllowance, nightPay, overtimePay, deduction, totalSalary);

        return new PayrollResponseDTO(
                requestDTO.getUserId(),
                userRepository.findNameByUserId(requestDTO.getUserId()),
                basicSalary,
                weeklyAllowance,
                overtimePay,
                nightPay,
                deduction,
                totalSalary,
                contract.getHourlyWage()
        );
    }

    // 근무자 리스트와 급여 정보를 포함한 데이터 생성
    public List<EmployeeDTO> getEmployeeListWithPayroll() {
        log.info("getEmployeeListWithPayroll 호출");

        List<Object[]> results = payrollRepository.findPayRollWithWorkAndUser();
        List<EmployeeDTO> employeeList = new ArrayList<>();

        for (Object[] result : results) {
            PayRoll payRoll = (PayRoll) result[0];
            Work work = (Work) result[1];
            User user = (User) result[2];

            // 근무 기록 조회
            List<Attendance> attendanceList = findAttendanceData(user.getUserId(), work.getWorkId(), LocalDate.now().minusMonths(1), LocalDate.now());

            // 계약 정보 조회
            Contract contract = findContractByWorkOrUser(user.getUserId(), work.getWorkId(), LocalDate.now().minusMonths(1), LocalDate.now());
            if (contract == null) {
                log.warn("유효한 계약 정보를 찾을 수 없습니다. Work ID: {}", work.getWorkId());
                continue;
            }

            // 급여 계산
            double basicSalary = calculateBasicSalary(attendanceList, contract);
            double weeklyAllowance = calculateWeeklyAllowance(attendanceList, contract.getHourlyWage());
            double nightPay = calculateNightPay(attendanceList, contract.getHourlyWage());
            double overtimePay = calculateOvertimePay(attendanceList, contract.getHourlyWage());
            double deduction = (basicSalary + weeklyAllowance + nightPay + overtimePay) * 0.1;
            double totalSalary = basicSalary + weeklyAllowance + nightPay + overtimePay - deduction;

            // 월 근무시간 및 월 근무일수 계산
            double monthlyHours = calculateMonthlyHours(attendanceList);
            int workDays = calculateWorkDays(attendanceList);

            // EmployeeDTO 생성
            EmployeeDTO employeeDTO = new EmployeeDTO(
                    user.getUserId(),
                    user.getName(),
                    work.getHireDate().toString(),
                    String.valueOf(contract.getHourlyWage()),
                    monthlyHours,
                    workDays,
                    basicSalary,
                    totalSalary,
                    payRoll.isPaid(),
                    payRoll.getPayRollId(),
                    weeklyAllowance,
                    nightPay,
                    overtimePay,
                    deduction
            );
            log.info("EmployeeDTO 생성: {}", employeeDTO);

            employeeList.add(employeeDTO);
        }
        log.info("최종 근무자 리스트: {}", employeeList);
        return employeeList;
    }

    // 기본급 계산
    private double calculateBasicSalary(List<Attendance> attendanceList, Contract contract) {
        double basicSalary = 0.0;
        for (Attendance attendance : attendanceList) {
            int regularMinutes = attendance.getRecognizedWorkMinute();
            basicSalary += (regularMinutes / 60.0) * contract.getHourlyWage();
        }
        log.info("기본급 계산 완료: {}", basicSalary);
        return basicSalary;
    }

    // 주휴수당 계산
    private double calculateWeeklyAllowance(List<Attendance> attendanceList, double hourlyWage) {
        double totalMinutes = 0;
        for (Attendance attendance : attendanceList) {
            totalMinutes += attendance.getRecognizedWorkMinute();
        }
        double avgWeeklyHours = (totalMinutes / 60.0) / 4.0;

        boolean isFullAttendance = attendanceList.stream()
                .allMatch(att -> "정상".equals(att.getIsNormalAttendance()));

        double weeklyAllowance = (avgWeeklyHours >= 15 && isFullAttendance) ? hourlyWage * 8 : 0;
        log.info("주휴수당 계산 완료: {}", weeklyAllowance);
        return weeklyAllowance;
    }

    // 야간수당 계산
    private double calculateNightPay(List<Attendance> attendanceList, double hourlyWage) {
        double nightPay = 0.0;
        for (Attendance attendance : attendanceList) {
            int nightMinutes = calculateNightMinutes(attendance);
            nightPay += (nightMinutes / 60.0) * hourlyWage * 0.5;
        }
        log.info("야간수당 계산 완료: {}", nightPay);
        return nightPay;
    }

    // 야간 근무 시간 계산
    private int calculateNightMinutes(Attendance attendance) {
        LocalDateTime startDateTime = attendance.getActualStart();
        LocalDateTime endDateTime = attendance.getActualEnd();

        LocalDateTime nightStart = startDateTime.toLocalDate().atTime(22, 0);
        LocalDateTime nightEnd = startDateTime.toLocalDate().plusDays(1).atTime(6, 0);

        if (startDateTime.isBefore(nightEnd) && endDateTime.isAfter(nightStart)) {
            LocalDateTime effectiveStart = startDateTime.isAfter(nightStart) ? startDateTime : nightStart;
            LocalDateTime effectiveEnd = endDateTime.isBefore(nightEnd) ? endDateTime : nightEnd;
            return (int) Duration.between(effectiveStart, effectiveEnd).toMinutes();
        }
        return 0;
    }

    // 월 근무시간 계산
    public double calculateMonthlyHours(List<Attendance> attendanceList) {
        double totalMinutes = 0.0;
        for (Attendance attendance : attendanceList) {
            totalMinutes += attendance.getRecognizedWorkMinute();
        }
        double monthlyHours = totalMinutes / 60.0;
        log.info("월 근무시간 계산 완료: {}", monthlyHours);
        return monthlyHours;
    }

    // 월 근무일수 계산
    private int calculateWorkDays(List<Attendance> attendanceList) {

        log.info("근무 일수 계산 시작 - Attendance List Size: {}", attendanceList.size());

        // 출근 데이터가 없는 경우 처리
        if (attendanceList.isEmpty()) {
            log.warn("근무 일수 계산 불가 - Attendance 데이터가 없습니다.");
            return 0;
        }

        // 고유 날짜 계산
        Set<LocalDate> uniqueDays = new HashSet<>();
        for (Attendance attendance : attendanceList) {
            if (attendance.getActualStart() != null) {
                uniqueDays.add(attendance.getActualStart().toLocalDate());
            }
            if (attendance.getActualEnd() != null) {
                uniqueDays.add(attendance.getActualEnd().toLocalDate());
            }
        }

        for (Attendance attendance : attendanceList) {
            if (attendance.getActualStart() != null) {
                LocalDate startDate = attendance.getActualStart().toLocalDate();
                uniqueDays.add(startDate);
                log.info("고유 날짜 추가 - Start Date: {}", startDate);
            }
            if (attendance.getActualEnd() != null) {
                LocalDate endDate = attendance.getActualEnd().toLocalDate();
                uniqueDays.add(endDate);
                log.info("고유 날짜 추가 - End Date: {}", endDate);
            }
        }

        log.info("고유 근무일 계산 완료 - Unique Days: {}", uniqueDays);
        int workDays = uniqueDays.size();
        log.info("근무 일수 계산 완료 - Work Days: {}", workDays);
        return workDays;
    }



    // 계약 정보 조회
    private Contract findContractByWorkOrUser(String userId, Integer workId, LocalDate startDate, LocalDate endDate) {
        log.info("계약 정보 조회 - User ID: {}, Start Date: {}, End Date: {}", userId, startDate, endDate);
        if (workId != null) {
            return contractRepository.findByWorkId(workId);
        }
        Contract contract = contractRepository.findValidContractByUserIdAndDateRange(userId, startDate, endDate);
        log.info("조회된 계약 정보: {}", contract);
        return contract;
    }

    // 출근 데이터 조회
    public List<Attendance> findAttendanceData(String userId, Integer workId, LocalDate startDate, LocalDate endDate) {
        log.info("출근 데이터 조회 - User ID: {}, Work ID: {}, Start Date: {}, End Date: {}", userId, workId, startDate, endDate);
        List<Attendance> attendanceList;
        if (workId != null) {
            attendanceList = attendanceRepository.findAttendancesByWorkIdAndDateRange(workId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            attendanceList = attendanceRepository.findAttendancesByDateRange(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }

        if (attendanceList.isEmpty()) {
            log.warn("출근 데이터 없음 - User ID: {}, Work ID: {}, Start Date: {}, End Date: {}", userId, workId, startDate, endDate);
        } else {
            log.info("출근 데이터 조회 성공 - {}건", attendanceList.size());
        }
        return attendanceList;
    }

    // 추가근무 수당 계산
    private double calculateOvertimePay(List<Attendance> attendanceList, double hourlyWage) {
        double overtimePay = 0.0;
        for (Attendance attendance : attendanceList) {
            int overtimeMinutes = attendance.getOvertimeMinute(); // 초과 근무 시간 (분)
            overtimePay += (overtimeMinutes / 60.0) * hourlyWage * 1.5; // 초과 근무 수당 1.5배
        }
        log.info("추가근무 수당 계산 완료: {}", overtimePay);
        return overtimePay;
    }

}
