package com.example.FinalProject.controller.employment;

import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.employment.ContractRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private WorkChangeRepository workChangeRepository;


    // 모든 계약 정보를 가져옵니다 //이거 말고 회사별로 써야함
    @GetMapping("/contracts")
    public List<Contract> getAllContracts() {
        //return contractRepository.findAll();
        return contractRepository.findAllContractsWithWorkAndUser(); //fetch join
    }

    //회사별 계약 정보
    @GetMapping("/contracts/company/{companyId}")
    public List<Contract> getContractsByCompanyId(@PathVariable Integer companyId) {
        return contractRepository.findAllActiveContractsByCompanyId(companyId);
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

//    // ID를 통해 특정 계약을 삭제합니다
//    @DeleteMapping("/contracts/{id}")
//    public ResponseEntity<Void> deleteContract(@PathVariable Integer id) {
//        if (contractRepository.existsById(id)) {
//            contractRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


    //ID를 통해 특정 계약을 삭제 또는 비활성화
    //@Transactional
    @DeleteMapping("/contracts/{id}")
    public ResponseEntity<Void> deleteOrDeactivateContract(@PathVariable Integer id) {
        // 1. 계약 조회
        Optional<Contract> optionalContract = contractRepository.findById(id);
        if (optionalContract.isEmpty()) {
            return ResponseEntity.notFound().build(); // 계약이 없으면 404 반환
        }

        Contract contract = optionalContract.get();

        // 2. 연결된 스케줄 조회(T, F 둘다 조회)
        boolean hasSchedule = scheduleRepository.existsByContract_ContractId(id);

        if (hasSchedule) {
            // 3. 스케줄 상태를 "F"로 변경(T만 가져와서 F로 변경)
            List<Schedule> schedules = scheduleRepository.findSchedulesByContractIdWithContractWorkAndUser(id);
            for (Schedule schedule : schedules) {
                schedule.setStatus("F");
                scheduleRepository.save(schedule);
            }

            // 4. 계약 상태를 "F"로 변경
            contract.setStatus("F");
            Contract savedContract = contractRepository.save(contract);
            System.out.println("Updated contract status: " + savedContract.getStatus());

            // 5. 상태를 변경한 후 200 OK 반환
            return ResponseEntity.ok().build();
        } else {
            // 6. 연결된 스케줄이 없으면 계약 삭제
            contractRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 삭제 완료 시 204 반환
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

        if (schedule.getContract() != null && schedule.getContract().getContractId() != null) {

            Contract contract = contractRepository.findById(schedule.getContract().getContractId())
                    .orElseThrow(() -> new IllegalArgumentException("EmploymentController createSchedule - Invalid contractId: " + schedule.getContract().getContractId()));
            //contractId로 데이터베이스에서 Contract를 찾지 못했을 경우 예외를 발생

            schedule.setContract(contract); // Contract 객체 설정
        }

        schedule.setStatus("T");

        return scheduleRepository.save(schedule);
    }

    // ID를 통해 기존 스케줄 정보를 업데이트합니다
    @PutMapping("/schedules/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Integer id, @RequestBody Schedule scheduleDetails) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
        //System.out.println(scheduleDetails);
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


    //삭제 대신 사용할 스케쥴 상태 비활성화
//    @PutMapping("/schedules/{id}/deactivate")
//    public ResponseEntity<Schedule> deactivateSchedule(@PathVariable Integer id) {
//        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
//
//        if (optionalSchedule.isPresent()) {
//            Schedule schedule = optionalSchedule.get();
//            schedule.setStatus("F"); // 상태를 비활성화로 변경
//            Schedule updatedSchedule = scheduleRepository.save(schedule);
//            return ResponseEntity.ok(updatedSchedule); // 변경된 스케줄 반환
//        } else {
//            return ResponseEntity.notFound().build(); // 스케줄을 찾지 못한 경우
//        }
//    }




    // ID를 통해 특정 스케줄을 삭제합니다
//    @DeleteMapping("/schedules/{id}")
//    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
//        if (scheduleRepository.existsById(id)) {
//            scheduleRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteOrDeactivateSchedule(@PathVariable Integer id) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);

        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();

            // 출결 데이터가 있는지 확인
            boolean hasAttendance = attendanceRepository.existsBySchedule_ScheduleId(schedule.getScheduleId());
            boolean hasWorkChange = workChangeRepository.existsBySchedule_ScheduleId(schedule.getScheduleId());


            if (hasAttendance || hasWorkChange) {
                // 출결 또는 WorkChange 데이터가 있으면 상태를 "F"로 설정
                schedule.setStatus("F");
                scheduleRepository.save(schedule); // 데이터베이스에 상태 변경 저장
                return ResponseEntity.ok().build(); // 성공 응답 반환
            } else {
                // 출결 데이터가 없으면 스케줄을 삭제
                scheduleRepository.delete(schedule);
                return ResponseEntity.noContent().build(); // 삭제 성공 응답 반환
            }
        } else {
            return ResponseEntity.notFound().build(); // 스케줄을 찾을 수 없는 경우
        }
    }


}
