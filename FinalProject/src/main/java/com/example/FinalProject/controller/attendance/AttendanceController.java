package com.example.FinalProject.controller.attendance;

import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;

    // 스케줄 ID로 출석 정보를 가져옵니다
    @GetMapping("/schedules/{scheduleId}/attendances")
    public List<Attendance> getAttendancesBySchedule(@PathVariable Integer scheduleId) {
        return attendanceRepository.findByScheduleScheduleId(scheduleId);
    }

    // WorkChange ID로 출석 정보를 가져옵니다
    @GetMapping("/workchanges/{changeId}/attendances")
    public List<Attendance> getAttendancesByWorkChange(@PathVariable Integer changeId) {
        return attendanceRepository.findByWorkChangeChangeId(changeId);
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
            attendance.setIsNormalAttendance(attendanceDetails.getIsNormalAttendance());
            attendance.setRecognizedWorkMinute(attendanceDetails.getRecognizedWorkMinute());
            attendance.setOvertimeStatus(attendanceDetails.getOvertimeStatus());
            attendance.setOvertimeMinute(attendanceDetails.getOvertimeMinute());
            attendance.setTotalMinute(attendanceDetails.getTotalMinute());
            attendance.setSchedule(attendanceDetails.getSchedule());
            attendance.setWorkChange(attendanceDetails.getWorkChange());
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
