package com.example.FinalProject.controller.commute;


import com.example.FinalProject.entity.commute.Attendance;
import com.example.FinalProject.repository.commute.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CommuteController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // 계약 및 스케줄을 통해 출석 정보를 가져옵니다
    @GetMapping("/contracts/{contractId}/schedules/{scheduleId}/attendances")
    public List<Attendance> getAttendancesByContractAndSchedule(
            @PathVariable Integer contractId,
            @PathVariable Integer scheduleId) {
        return attendanceRepository.findByContractIdAndScheduleId(contractId, scheduleId);
    }

    // 새로운 출석을 생성합니다
    @PostMapping("/attendances")
    public Attendance createAttendance(@RequestBody Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    // ID를 통해 특정 출석 정보를 가져옵니다
    @GetMapping("/attendances/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Integer id) {
        Optional<Attendance> attendance = attendanceRepository.findById(id);
        return attendance.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ID를 통해 기존 출석 정보를 업데이트합니다
    @PutMapping("/attendances/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Integer id, @RequestBody Attendance attendanceDetails) {
        Optional<Attendance> optionalAttendance = attendanceRepository.findById(id);
        if (optionalAttendance.isPresent()) {
            Attendance attendance = optionalAttendance.get();
            attendance.setActualStart(attendanceDetails.getActualStart());
            attendance.setActualEnd(attendanceDetails.getActualEnd());
            attendance.setCommuteStatus(attendanceDetails.getCommuteStatus());
            attendance.setRemark(attendanceDetails.getRemark());
            attendance.setUserId(attendanceDetails.getUserId());
            attendance.setIsNormalAttendance(attendanceDetails.getIsNormalAttendance());
            attendance.setRecognizedWorkHours(attendanceDetails.getRecognizedWorkHours());
            attendance.setOvertimeStatus(attendanceDetails.getOvertimeStatus());
            attendance.setOvertimeHours(attendanceDetails.getOvertimeHours());
            attendance.setTotalHours(attendanceDetails.getTotalHours());
            return ResponseEntity.ok(attendanceRepository.save(attendance));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ID를 통해 특정 출석 정보를 삭제합니다
    @DeleteMapping("/attendances/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Integer id) {
        if (attendanceRepository.existsById(id)) {
            attendanceRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
