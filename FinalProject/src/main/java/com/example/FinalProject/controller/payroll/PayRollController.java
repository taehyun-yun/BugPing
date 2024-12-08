package com.example.FinalProject.controller.payroll;

import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.service.payroll.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculator")
public class PayRollController {

    @Autowired
    private PayrollService payrollService;

    @PostMapping("/calculate")
    public ResponseEntity<PayrollResponseDTO> calculatePayroll(@RequestBody PayrollRequestDTO request) {
        PayrollResponseDTO response = payrollService.calculatePayroll(request);
        return ResponseEntity.ok(response);
    }
}