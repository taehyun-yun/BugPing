package com.example.FinalProject.controller.employment;

import com.example.FinalProject.repository.employment.ContractRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EmploymentController {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Contract 엔드포인트

    // 모든 계약 정보를 가져옵니다
    @GetMapping("/contracts")
    public List<Contract> getAllContracts() {
        //return contractRepository.findAll();
        return contractRepository.findAllContractsWithWorkAndUser(); //fetch join
    }

    // ID를 통해 특정 계약 정보를 가져옵니다
    @GetMapping("/contracts/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Integer id) {
        // Optional<Contract> contract = contractRepository.findById(id); //Optional - isEmpty 체크필요
        // return contract.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        Contract contract = contractRepository.findContractWithWorkAndUser(id);//fetch join

        // 계약 정보가 존재하지 않을 경우 404 반환
        if (contract == null) {
            return ResponseEntity.notFound().build();
        }

        // 계약 정보가 존재할 경우 200 OK와 함께 반환
        return ResponseEntity.ok(contract);
    }

    // 새로운 계약을 생성합니다
    @PostMapping("/contracts")
    public Contract createContract(@RequestBody Contract contract) {
        return contractRepository.save(contract);
    }

    // ID를 통해 기존 계약 정보를 업데이트합니다
    @PutMapping("/contracts/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable Integer id, @RequestBody Contract contractDetails) {
        Optional<Contract> optionalContract = contractRepository.findById(id);
        if (optionalContract.isPresent()) {
            Contract contract = optionalContract.get();
            contract.setWork(contractDetails.getWork());
            contract.setHourlyWage(contractDetails.getHourlyWage());
            contract.setContractStart(contractDetails.getContractStart());
            contract.setContractEnd(contractDetails.getContractEnd());
            return ResponseEntity.ok(contractRepository.save(contract));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ID를 통해 특정 계약을 삭제합니다
    @DeleteMapping("/contracts/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Integer id) {
        if (contractRepository.existsById(id)) {
            contractRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Schedule 엔드포인트

    // 모든 스케줄 정보를 가져옵니다
    @GetMapping("/schedules")
    public List<Schedule> getAllSchedules() {
        //return scheduleRepository.findAll();
        return scheduleRepository.findAllSchedulesWithContractWorkAndUser();//fetch join
    }

    // 특정 계약과 연관된 스케줄 정보를 가져옵니다
    @GetMapping("/contracts/{contractId}/schedules")
    public List<Schedule> getSchedulesByContractId(@PathVariable Integer contractId) {
        //return scheduleRepository.findByContractContractId(contractId);
        return scheduleRepository.findSchedulesByContractIdWithContractWorkAndUser(contractId);//fetch join
    }

    // ID를 통해 특정 스케줄 정보를 가져옵니다
    @GetMapping("/schedules/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Integer id) {
    // Optional<Schedule> schedule = scheduleRepository.findById(id);
    // return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

        Schedule schedule = scheduleRepository.findScheduleWithContractWorkAndUser(id);//fetch join

        // 계약 정보가 존재하지 않을 경우 404 반환
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }

        // 계약 정보가 존재할 경우 200 OK와 함께 반환
        return ResponseEntity.ok(schedule);
    }

    // 새로운 스케줄을 생성합니다
    @PostMapping("/schedules")
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // ID를 통해 기존 스케줄 정보를 업데이트합니다
    @PutMapping("/schedules/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Integer id, @RequestBody Schedule scheduleDetails) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
        System.out.println(scheduleDetails);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setDay(scheduleDetails.getDay());
            schedule.setOfficialStart(scheduleDetails.getOfficialStart());
            schedule.setOfficialEnd(scheduleDetails.getOfficialEnd());
            schedule.setBreakMinute(scheduleDetails.getBreakMinute());
            return ResponseEntity.ok(scheduleRepository.save(schedule));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ID를 통해 특정 스케줄을 삭제합니다
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
