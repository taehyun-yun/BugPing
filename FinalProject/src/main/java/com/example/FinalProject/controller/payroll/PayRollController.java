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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<List<EmployeeDTO>> getEmployeeList(
            @RequestParam(defaultValue = "") String searchQuery,
            @RequestParam(defaultValue = "") String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        // 로그인된 사용자 ID 가져오기
        String loggedInUserId = jwtService.getLoggedInUserId();
        if (loggedInUserId == null) {
            log.error("로그인된 사용자 정보를 가져올 수 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("로그인된 사용자 ID!!: {}", loggedInUserId);
        log.info("정렬 요청 - Field: {}, Direction: {}", sortField, sortDirection);


        // 근무자 리스트 조회
        List<EmployeeDTO> employeeList = payrollService.getEmployeeListWithPayroll(loggedInUserId, searchQuery, sortField, sortDirection);

        log.info("계산된 근무자 리스트 데이터: {}", employeeList);
        return ResponseEntity.ok(employeeList);
    }

}