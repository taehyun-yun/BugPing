package com.example.FinalProject.controller.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.payroll.PayRoll;
import com.example.FinalProject.repository.employment.ContractRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ContractRepository contractRepository;

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

    // ContractDB 조회 후 새로운 데이터 생성
    @PostMapping("/generate")
    public ResponseEntity<String> generatePayrollForAllContracts() {
        payrollService.generatePayrollForAllContracts();
        return ResponseEntity.ok("PayRoll 데이터 생성 완료");
    }

    // 근무자 리스트 정보 반환
    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getEmployeeList(
            @RequestParam(defaultValue = "") String searchQuery,
            @RequestParam(defaultValue = "") String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) Integer companyId) {


        if (companyId == null) {
            log.error("회사 ID가 전달되지 않았습니다.");
            return ResponseEntity.badRequest().body(Map.of("error", "회사 ID가 필요합니다."));
        }

        log.info("정렬 요청 - Field: {}, Direction: {}", sortField, sortDirection);
        log.info("회사 아이디==========================: {}", companyId);

        // 근무자 리스트 조회
        List<EmployeeDTO> employeeList = payrollService.getEmployeeListWithPayroll(companyId, searchQuery, sortField, sortDirection);

        log.info("계산된 근무자 리스트 데이터: {}", employeeList);
        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("employees", employeeList);
        response.put("companyId", companyId); // companyId를 응답에 포함

        return ResponseEntity.ok(response);
    }

}