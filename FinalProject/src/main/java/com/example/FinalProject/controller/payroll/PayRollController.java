package com.example.FinalProject.controller.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollRequestDTO;
import com.example.FinalProject.dto.payrollDTO.PayrollResponseDTO;
import com.example.FinalProject.entity.payroll.PayRoll;
import com.example.FinalProject.repository.payroll.PayrollRepository;
import com.example.FinalProject.service.payroll.PayrollService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api")
public class PayRollController {

    @Autowired
    private PayrollService payrollService;
    @Autowired
    private PayrollRepository payrollRepository;

    // 급여 계산
    @PostMapping("/payroll")
    public ResponseEntity<PayrollResponseDTO> calculatePayroll(@RequestBody PayrollRequestDTO request) {
        log.info("요청받은 데이터: userId={}, startDate={}, endDate={}",
                request.getUserId(), request.getStartDate(), request.getEndDate());
        PayrollResponseDTO response = payrollService.calculatePayroll(request);
        return ResponseEntity.ok(response);
    }

    // 지급 상태 업데이트
    @PatchMapping("/payroll/{payRollId}/paid")
    public ResponseEntity<Void> updatePayrollStatus(@PathVariable("payRollId") Integer payRollId, @RequestParam boolean isPaid) {
        PayRoll payRoll = payrollRepository.findById(payRollId)
                .orElseThrow(() -> new IllegalArgumentException("지급 정보를 찾을 수 없습니다 ID : " + payRollId));
        payRoll.setPaid(isPaid); // 지급 상태 업데이트
        payrollRepository.save(payRoll); // 저장
        return ResponseEntity.noContent().build(); // 응답
    }

    // 근무자 리스트 정보 반환
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeeList() {
        List<EmployeeDTO> employeeList = payrollService.getEmployeeListWithPayroll();
        return ResponseEntity.ok(employeeList);
    }

    // 페이징 관련 메서드
    @GetMapping("/employees/paging")
    public ResponseEntity<Page<Integer>> getEmployeePaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Integer> employeeIds = payrollService.getEmployeeIds(pageable);
        return ResponseEntity.ok(employeeIds);
    }
}