package com.example.FinalProject.controller.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.repository.payroll.EmployeeRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.payroll.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PayRollController {

    @Autowired
    private PayrollService payrollService;
    @Autowired
    private UserRepository userRepository;

    // 급여 계산
    @PostMapping("/payroll")
    public ResponseEntity<PayrollResponseDTO> calculatePayroll(@RequestBody PayrollRequestDTO request) {
        PayrollResponseDTO response = payrollService.calculatePayroll(request);
        return ResponseEntity.ok(response);
    }

    // 근무자 리스트 및 대략적인 급여 정보 반환
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeeList() {
        List<EmployeeDTO> employeeList = payrollService.getEmployeeListWithPayroll();
        return ResponseEntity.ok(employeeList);
    }

    // 페이징
    @GetMapping("employee-paging")
    public ResponseEntity<Page<String>> getPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<String> employeeNames = userRepository.findAllNames(pageable); // 이름만 가져오기
        return ResponseEntity.ok(employeeNames);
    }
}