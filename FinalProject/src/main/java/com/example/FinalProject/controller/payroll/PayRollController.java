package com.example.FinalProject.controller.payroll;

import com.example.FinalProject.entity.payroll.PayRoll;
import com.example.FinalProject.service.payroll.PayRollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayRollController {

    @Autowired
    private PayRollService payRollService;

    @GetMapping("/all")
    public ResponseEntity<List<PayRoll>> getAllPayRolls() {
        List<PayRoll> payRolls = payRollService.getAllPayRolls();
        return ResponseEntity.ok(payRolls);
    }
}
