package com.example.FinalProject.controller.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.service.payroll.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PayRollController {

    @Autowired
    private PayrollService payrollService;

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
}