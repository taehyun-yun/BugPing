package com.example.FinalProject.service.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.payroll.PayRoll;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ContractRepository;
import com.example.FinalProject.repository.payroll.PayrollRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkRepository workRepository;

    // 급여 계산 메서드
    public PayrollResponseDTO calculatePayroll(PayrollRequestDTO requestDTO) {
        log.info("급여 계산 요청 - User ID: {}, Start Date: {}, End Date: {}",
                requestDTO.getUserId(), requestDTO.getStartDate(), requestDTO.getEndDate());

        // 계약 정보 조회
        Contract contract = findContractByWorkOrUser(requestDTO.getUserId(), null, requestDTO.getStartDate(), requestDTO.getEndDate());
        if (contract == null) {
            throw new IllegalArgumentException("유효한 계약 정보를 찾을 수 없습니다.");
        }

        // 요청된 급여 기간과 계약 기간의 교집합 계산
        LocalDateTime payrollStart = requestDTO.getStartDate().isBefore(contract.getContractStart())
                ? contract.getContractStart()
                : requestDTO.getStartDate();
        LocalDateTime payrollEnd = requestDTO.getEndDate().isAfter(contract.getContractEnd())
                ? contract.getContractEnd()
                : requestDTO.getEndDate();

        if (payrollStart.isAfter(payrollEnd)) {
            throw new IllegalArgumentException("요청된 급여 기간이 계약 기간과 겹치지 않습니다.");
        }

        log.info("급여 계산 대상 기간: {} ~ {}", payrollStart, payrollEnd);

        // 출근 데이터 조회
        List<Attendance> attendanceList = findAttendanceData(requestDTO.getUserId(), null, payrollStart, payrollEnd);

        if (attendanceList.isEmpty()) {
            log.warn("출근 데이터가 없어 급여 계산을 중단합니다. User ID: {}", requestDTO.getUserId());
            return createEmptyPayrollResponse(requestDTO); // 기본값 반환
        }

        // 출근 데이터 유효성 검사
        boolean isValid = false;
        for (Attendance attendance : attendanceList) {
            String userId = attendance.getSchedule().getContract().getWork().getUser().getUserId();
            if (userId.equals(requestDTO.getUserId())) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            log.error("출근 데이터가 요청된 사용자와 일치하지 않습니다. User ID: {}, Attendance List: {}", requestDTO.getUserId(), attendanceList);
            throw new IllegalArgumentException("잘못된 출근 데이터가 감지되었습니다.");
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
                contract.getHourlyWage(),
                requestDTO.getStartDate()
        );
    }

    // 근무자 리스트와 급여 정보를 포함한 데이터 생성
    public Page<EmployeeDTO> getEmployeeListWithPayroll(String loggedInUserId, int page, int size) {
        log.info("페이징 처리된 근무자 리스트 요청 - Page: {}, Size: {}", page, size);

        // 로그인된 사용자 ID로 회사 조회
        Company company = companyRepository.findByUserId(loggedInUserId);
        if (company == null) {
            log.error("해당 사용자와 연결된 회사 정보를 찾을 수 없습니다. User ID: {}", loggedInUserId);
            throw new IllegalArgumentException("해당 사용자와 연결된 회사 정보를 찾을 수 없습니다.");
        }
        log.info("조회된 회사 정보: {}", company);

        // 페이징된 Work 리스트 조회
        Pageable pageable = PageRequest.of(page, size);
        Page<Work> workPage = workRepository.findAllByCompany_CompanyId(company.getCompanyId(), pageable);

        List<EmployeeDTO> employeeList = new ArrayList<>();

        // for 문으로 EmployeeDTO 생성
        for (Work work : workPage.getContent()) {
            User user = work.getUser();

            // 계약 정보 조회
            Contract contract = findContractByWorkOrUser(
                    user.getUserId(),
                    work.getWorkId(),
                    LocalDateTime.now().minusMonths(1),
                    LocalDateTime.now()
            );

            if (contract == null) {
                log.warn("유효한 계약 정보를 찾을 수 없습니다. Work ID: {}", work.getWorkId());
                continue; // 계약 정보가 없으면 현재 루프를 건너뛰기
            }

            // 근무 기록 조회
            List<Attendance> attendanceList = findAttendanceData(
                    user.getUserId(),
                    work.getWorkId(),
                    LocalDateTime.now().minusMonths(1),
                    LocalDateTime.now()
            );

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
                    false, // 기본 지급 상태
                    null, // Payroll ID
                    weeklyAllowance,
                    nightPay,
                    overtimePay,
                    deduction
            );

            log.info("EmployeeDTO 생성: {}", employeeDTO);
            employeeList.add(employeeDTO);
        }

        log.info("페이징된 최종 근무자 리스트 생성 완료: {}", employeeList.size());

        // PageImpl로 페이징된 결과 반환
        return new PageImpl<>(employeeList, pageable, workPage.getTotalElements());
    }

    // 기본급 계산
    private double calculateBasicSalary(List<Attendance> attendanceList, Contract contract) {
        if (attendanceList.isEmpty()) {
            log.warn("출근 데이터가 없어 기본급을 계산할 수 없습니다.");
            return 0.0; // 기본값 반환
        }

        double basicSalary = 0.0;

        for (Attendance attendance : attendanceList) {
            int regularMinutes = attendance.getRecognizedWorkMinute() - attendance.getRecognizedWorkBreakMinute();
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
        log.info("주간 평균 시간 : {}", avgWeeklyHours);


        boolean isFullAttendance = attendanceList.stream()
                .allMatch(att -> "정상".equals(att.getIsNormalAttendance()));
        log.info("정상 출근 여부: {}", isFullAttendance);

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
private Contract findContractByWorkOrUser(
        String userId, Integer workId, LocalDateTime startDate, LocalDateTime endDate) {
    log.info("계약 정보 조회 - User ID: {}, Work ID: {}, Start Date: {}, End Date: {}", userId, workId, startDate, endDate);

    // Work ID가 있을 때 먼저 조회
    if (workId != null) {
        List<Contract> contracts = contractRepository.findAllByWorkId(workId);
        if (!contracts.isEmpty()) {
            log.info("Work ID로 조회된 계약: {}", contracts.get(0));
            return contracts.get(0); // 첫 번째 계약 반환
        }
        log.warn("해당 Work ID에 대한 계약이 존재하지 않습니다. Work ID: {}", workId);
    }

    // Work ID가 없거나 계약이 없으면 User ID와 날짜 범위로 조회
    List<Contract> contractsByUser = contractRepository.findAllValidContractsByUserIdAndDateRange(userId, startDate, endDate);
    if (!contractsByUser.isEmpty()) {
        log.info("User ID와 날짜 범위로 조회된 계약: {}", contractsByUser.get(0));
        return contractsByUser.get(0); // 첫 번째 계약 반환
    }

    // 계약 정보가 없을 때
    log.warn("해당 조건에 맞는 계약 정보가 없습니다. User ID: {}, Work ID: {}", userId, workId);
    return null; // 예외 대신 null 반환
}

    // 출근 데이터 조회
    public List<Attendance> findAttendanceData(String userId, Integer workId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("출근 데이터 조회 - User ID: {}, Work ID: {}, Start Date: {}, End Date: {}", userId, workId, startDate, endDate);
        List<Attendance> attendanceList;
        if (workId != null) {
            attendanceList = attendanceRepository.findAttendancesByWorkIdAndDateRange(workId, startDate, endDate);
        } else {
            attendanceList = attendanceRepository.findAttendancesByUserIdAndDateRange(userId, startDate, endDate);
        }

        if (attendanceList.isEmpty()) {
            log.warn("출근 데이터 없음 - User ID: {}, Work ID: {}, Start Date: {}, End Date: {}", userId, workId, startDate, endDate);
        } else {
            log.info("출근 데이터 조회 성공 - {}건", attendanceList.size());
        }
        log.info("Attendance List: {}", attendanceList);
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

    private PayrollResponseDTO createEmptyPayrollResponse(PayrollRequestDTO requestDTO) {
        log.info("기본값으로 PayrollResponseDTO 생성 - User ID: {}", requestDTO.getUserId());
        return new PayrollResponseDTO(
                requestDTO.getUserId(),
                userRepository.findNameByUserId(requestDTO.getUserId()),
                0.0, // 기본급
                0.0, // 주휴수당
                0.0, // 초과근무수당
                0.0, // 야간근무수당
                0.0, // 공제
                0.0, // 총급여
                0.0, // 시급
                requestDTO.getStartDate() // 시작 날짜
        );
    }
}
