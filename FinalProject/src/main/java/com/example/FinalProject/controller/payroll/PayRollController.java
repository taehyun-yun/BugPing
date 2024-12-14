package com.example.FinalProject.controller.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.entity.payroll.PayRoll;
import com.example.FinalProject.repository.payroll.PayrollRepository;
import com.example.FinalProject.service.jwt.JwtService;
import com.example.FinalProject.service.jwt.JwtServiceImpl;
import com.example.FinalProject.service.payroll.PayrollService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api")
public class PayRollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private PayrollRepository payrollRepository;

    // 지급 상태 업데이트
    @PatchMapping("/payroll/{payRollId}/paid")
    public ResponseEntity<Void> updatePayrollStatus(@PathVariable("payRollId") Integer payRollId, @RequestParam boolean isPaid) {

        log.info("지급 상태 업데이트 요청 - PayRoll ID : {}, 상태 : {}", payRollId, isPaid);

        PayRoll payRoll = payrollRepository.findById(payRollId)
                .orElseThrow(() -> new IllegalArgumentException("지급 정보를 찾을 수 없습니다 ID : " + payRollId));
        payRoll.setPaid(isPaid); // 지급 상태 업데이트
        payrollRepository.save(payRoll); // 저장

        log.info("지급 상태 업데이트 완료 - PayRoll ID : {}, 상태 : {}", payRollId, isPaid);
        return ResponseEntity.noContent().build(); // 응답
    }

    // 근무자 리스트 정보 반환
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeeList() {

        // 로그인된 사용자 ID 가져오기
        String loggedInUserId = jwtService.getLoggedInUserId();
        if (loggedInUserId == null) {
            log.error("로그인된 사용자 정보를 가져올 수 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("로그인된 사용자 ID: {}", loggedInUserId);

        log.info("근무자 리스트 요청을 받았습니다.");

        // 근무자 리스트 조회
        List<EmployeeDTO> employeeList = payrollService.getEmployeeListWithPayroll(loggedInUserId);

        // 급여 계산을 각 직원에 대해 수행
        for (EmployeeDTO employee : employeeList) {
            try {
                // LocalDateTime으로 시작일과 종료일 생성
                LocalDateTime startDateTime = LocalDate.parse(employee.getStartDate())
                        .atStartOfDay(); // 시작일의 자정을 기준으로 LocalDateTime 생성
                LocalDateTime endDateTime = LocalDateTime.now()
                        .withHour(23).withMinute(59).withSecond(59); // 종료일은 현재 날짜의 끝 시간

                PayrollRequestDTO requestDTO = new PayrollRequestDTO(
                        employee.getEmployeeId(),
                        startDateTime, // LocalDateTime 시작일
                        endDateTime // LocalDateTime 종료일
                );
                PayrollResponseDTO responseDTO = payrollService.calculatePayroll(requestDTO);

                // EmployeeDTO 업데이트
                employee.setTotalSalary(responseDTO.getTotalSalary());
                employee.setBasicSalary(responseDTO.getBasicSalary());
                employee.setWeeklyAllowance(responseDTO.getWeeklyAllowance());
                employee.setNightPay(responseDTO.getNightPay());
                employee.setMonthlyHours(responseDTO.getBasicSalary() / responseDTO.getHourlyWage());
                employee.setOvertimePay(responseDTO.getOvertimePay());
                employee.setDeduction(responseDTO.getDeduction());

                log.info("최종 응답 데이터: {}", employeeList);

                log.info("급여 계산 완료 - Employee ID: {}, Total Salary: {}", employee.getEmployeeId(), responseDTO.getTotalSalary());
            } catch (Exception e) {
                log.error("급여 계산 실패 - Employee ID: {}", employee.getEmployeeId(), e);
            }
        }

        log.info("계산된 근무자 리스트 데이터: {}", employeeList);
        return ResponseEntity.ok(employeeList);
    }

}