package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/validate")
    public ResponseEntity<?> validateContractDate(
            @RequestParam Integer scheduleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

        LocalDate contractStart = schedule.getContract().getContractStart().toLocalDate();
        LocalDate contractEnd = schedule.getContract().getContractEnd().toLocalDate();

        boolean isValid = !date.isBefore(contractStart) && !date.isAfter(contractEnd);

        return ResponseEntity.ok(Collections.singletonMap("valid", isValid));
    }
}