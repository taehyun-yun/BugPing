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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // 급여 계산 유틸 메서드
    private double calculateTotalSalary(List<Attendance> attendanceList, Contract contract) {
        double basicSalary = 0; // 기본급
        double overtimePay = 0; // 추가 수당
        double nightPay = 0; // 야간 수당

        for (Attendance attendance : attendanceList) {
            int regularMinutes = attendance.getRecognizedWorkMinute(); // 정규 근무 시간
            int overtimeMinutes = attendance.getOvertimeMinute();      // 초과 근무 시간
            int nightMinutes = calculateNightMinutes(attendance);      // 야간 근무 시간

            basicSalary += (regularMinutes / 60.0) * contract.getHourlyWage();
            overtimePay += (overtimeMinutes / 60.0) * contract.getHourlyWage() * 1.5;
            nightPay += (nightMinutes / 60.0) * contract.getHourlyWage() * 0.5;
        }

        return basicSalary + overtimePay + nightPay;
    }

    // 계약 정보 조회 (Work ID 또는 User ID + Date Range)
    private Contract findContractByWorkOrUser(String userId, Integer workId, LocalDate startDate, LocalDate endDate) {
        if (workId != null) {
            return contractRepository.findByWorkId(workId);
        }
        return contractRepository.findValidContractByUserIdAndDateRange(userId, startDate, endDate);
    }

    // 출근 데이터 조회
    private List<Attendance> findAttendanceData(String userId, Integer workId, LocalDate startDate, LocalDate endDate) {
        if (workId != null) {
            return attendanceRepository.findAttendancesByUserIdAndWorkId(userId, workId);
        }
        return attendanceRepository.findAttendancesByDateRange(userId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    // 급여 계산 메서드
    public PayrollResponseDTO calculatePayroll(PayrollRequestDTO requestDTO) {

        if (requestDTO.getUserId() == null || requestDTO.getStartDate() == null || requestDTO.getEndDate() == null) {
            throw new IllegalArgumentException("필수 입력값(userId, startDate, endDate)이 누락되었습니다.");
        }

        // 출근 데이터 가져오기
        List<Attendance> attendanceList = findAttendanceData(
                requestDTO.getUserId(), null, requestDTO.getStartDate(), requestDTO.getEndDate()
        );

        if (attendanceList.isEmpty()) {
            throw new IllegalArgumentException("해당 기간에 대한 출근 데이터가 없습니다.");
        }

        // 계약 정보 가져오기
        Contract contract = findContractByWorkOrUser(requestDTO.getUserId(), null, requestDTO.getStartDate(), requestDTO.getEndDate());
        if (contract == null) {
            throw new IllegalArgumentException("유효한 계약 정보가 없습니다.");
        }
        log.info("조회 조건 - userId: {}, startDate: {}, endDate: {}", requestDTO.getUserId(), requestDTO.getStartDate(), requestDTO.getEndDate());
        log.info("조회된 계약 정보: contractId={}, hourlyWage={}",
                contract.getContractId(), contract.getHourlyWage());

        // 항목별 초기화
        double basicSalary = 0; // 기본급
        double overtimePay = 0; // 연장수당
        double nightPay = 0; // 야간수당

        // 근무시간 기반 급여 계산
        for (Attendance attendance : attendanceList) {
            int regularMinutes = attendance.getRecognizedWorkMinute(); // 정규 근무 시간 (분)
            int overtimeMinutes = attendance.getOvertimeMinute(); // 초과 근무 시간 (분)
            int nightMinutes = calculateNightMinutes(attendance); // 야간 근무 시간 (분)

            // 기본급, 초과 근무수당, 야간수당 계산
            basicSalary += (regularMinutes / 60.0) * contract.getHourlyWage(); // 기본급
            overtimePay += (overtimeMinutes / 60.0) * contract.getHourlyWage() * 1.5; // 초과 근무 1.5배
            nightPay += (nightMinutes / 60.0) * contract.getHourlyWage() * 0.5; // 야간 할증

            log.info("기본급 계산 - 분: {}, 기본급: {}", regularMinutes, (regularMinutes / 60.0) * contract.getHourlyWage());
            log.info("야간 수당 계산 - 분: {}, 야간수당: {}", nightMinutes, (nightMinutes / 60.0) * contract.getHourlyWage() * 1.5);
        }

        // 주휴수당, 공제액(일단 10%), 총급여 계산
        double weeklyAllowance = calculateWeeklyAllowance(attendanceList, contract.getHourlyWage());
        double deduction = (basicSalary + overtimePay + nightPay + weeklyAllowance) * 0.1;
        double totalSalary = basicSalary + overtimePay + nightPay + weeklyAllowance - deduction;

        log.info("급여 계산 완료 - 직원ID: {}, 기본급: {}, 주휴수당: {}, 연장수당: {}, 야간수당: {}, 실수령액: {}",
                requestDTO.getUserId(), basicSalary, weeklyAllowance, overtimePay, nightPay, totalSalary);

        // Payroll 엔티티 생성 및 저장
//        PayRoll payRoll = new PayRoll();
//        payRoll.setPaymentDate(LocalDate.now()); // 지급일자
//        payRoll.setPaid(false); // 초기 상태 미지급
//        payRoll.setWork(contract.getWork()); // work 엔티티 연관관계 설정
//
//        payrollRepository.save(payRoll);

        // 응답 DTO 생성
        return new PayrollResponseDTO(
                requestDTO.getUserId(),
                userRepository.findNameByUserId(requestDTO.getUserId()), // 사용자 이름
                basicSalary,
                weeklyAllowance,
                overtimePay,
                nightPay,
                deduction,
                totalSalary
        );
    } // end calculatePayroll

//    // 총 근무 시간 계산
//    private double calculateTotalWorkHours(List<AttendanceDTO> attendanceList) {
//        double totalMinutes = 0;
//        for (AttendanceDTO attendanceDTO : attendanceList) {
//            totalMinutes += attendanceDTO.getRecognizedWorkMinute();
//        }
//        return totalMinutes / 60.0; // 시간 단위로 반환
//    }// end calculateTotalWorkHours

    // 주휴수당 계산 메서드
    private double calculateWeeklyAllowance(List<Attendance> attendanceList, double hourlyWage) {
        double totalMinutes = 0;
        for (Attendance attendance : attendanceList) {
            totalMinutes += attendance.getRecognizedWorkMinute();
        }
        double avgWeeklyHours = (totalMinutes / 60.0) / 4.0;

        boolean isFullAttendance = true;
        for (Attendance attendance : attendanceList) {
            if (!"정상".equals(attendance.getIsNormalAttendance())) {
                isFullAttendance = false;
                break;
            }
        }
        return (avgWeeklyHours >= 15 && isFullAttendance) ? hourlyWage * 8 : 0;
    }

    // 야간 수당 메서드
    private int calculateNightMinutes(Attendance attendance) {
        LocalDateTime startDateTime = attendance.getActualStart();
        LocalDateTime endDateTime = attendance.getActualEnd();

        // 야간 근무 시간 기준 (22시 ~ 06시)
        LocalDateTime nightStart = startDateTime.toLocalDate().atTime(22, 0); // 당일 22:00
        LocalDateTime nightEnd = startDateTime.toLocalDate().plusDays(1).atTime(6, 0); // 다음 날 06:00

        //int nightMinutes = 0;

        // 근무시간이 야간 시간(22:00 - 06:00)과 겹치는지 확인
        if (startDateTime.isBefore(nightEnd) && endDateTime.isAfter(nightStart)) {
            // 겹치는 야간 시간 계산
            LocalDateTime effectiveStart = startDateTime.isAfter(nightStart) ? startDateTime : nightStart;
            LocalDateTime effectiveEnd = endDateTime.isBefore(nightEnd) ? endDateTime : nightEnd;
            return  (int) Duration.between(effectiveStart, effectiveEnd).toMinutes();
        }

        return 0;
    } // end calculateNightHours

    public List<EmployeeDTO> getEmployeeListWithPayroll() {
        // PayRoll, Work, User 데이터를 가져오기 위해 JPA 쿼리 실행
        List<Object[]> results = payrollRepository.findPayRollWithWorkAndUser();

        // 결과를 저장할 EmployeeDTO 리스트
        List<EmployeeDTO> employeeList = new ArrayList<>();

        for (Object[] result : results) {
            // 각 객체를 적절한 타입으로 캐스팅
            PayRoll payRoll = (PayRoll) result[0];
            Work work = (Work) result[1];
            User user = (User) result[2];

            // 근무 기록 조회
            List<Attendance> attendanceList = attendanceRepository.findAttendancesByUserIdAndWorkId(
                    user.getUserId(), work.getWorkId()
            );

            // 계약 정보 조회
            Contract contract = contractRepository.findByWorkId(work.getWorkId());

            // 급여 계산
            double totalSalary = calculateTotalSalary(attendanceList, contract);

            // EmployeeDTO 객체 생성 및 리스트에 추가
            EmployeeDTO employeeDTO = new EmployeeDTO(
                    user.getUserId(),                   // 직원 ID
                    user.getName(),                     // 직원 이름
                    work.getHireDate().toString(),      // 정산 시작일
                    String.valueOf(contract.getHourlyWage()), // 시급
                    attendanceList.size() * 8,          // 월 근무 시간
                    attendanceList.size(),              // 월 근무 일수
                    totalSalary,                        // 총 급여
                    payRoll.isPaid(),                   // 지급 여부
                    payRoll.getPayRollId()              // PayRoll ID
            );

            employeeList.add(employeeDTO);
        }

        return employeeList;
    }

    // 페이징 처리 메서드
    public Page<Integer> getEmployeeIds(Pageable pageable) {
        return userRepository.findAllEmployeeIds(pageable);
    }

}// end service