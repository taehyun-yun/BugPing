package com.example.FinalProject.controller.attendance;

import com.example.FinalProject.dto.AdminAttendanceDTO;
import com.example.FinalProject.dto.AttendanceDetailsDTO;
import com.example.FinalProject.entity.attendance.Attendance;
import com.example.FinalProject.repository.attendance.AttendanceRepository;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.service.employment.AttendanceService;
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

    @Autowired
    private AttendanceService attendanceService;

    private final CompanyRepository companyRepository;

    public AttendanceController(AttendanceService attendanceService, CompanyRepository companyRepository){
        this.attendanceService = attendanceService;
        this.companyRepository = companyRepository;
    }


    // 스케줄 ID로 출석 정보를 가져옵니다
    @GetMapping("/schedules/{scheduleId}/attendances")
    public List<Attendance> getAttendancesBySchedule(@PathVariable Integer scheduleId) {
        return attendanceRepository.findByScheduleScheduleId(scheduleId);
    }

    // WorkChange ID로 출석 정보를 가져옵니다
    @GetMapping("/workchanges/{workChangeId}/attendances")
    public List<Attendance> getAttendancesByWorkChange(@PathVariable Integer workChangeId) {
        return attendanceRepository.findByWorkChangeWorkChangeId(workChangeId);
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

    // 금일 출근자 리스트 조회
    @GetMapping("/attendances/attendancesList")
    public ResponseEntity<List<AdminAttendanceDTO>> getTodayAttendanceList() {
        List<AdminAttendanceDTO> attendanceList = attendanceService.getTodayAttendances();
        return ResponseEntity.ok(attendanceList);
    }

    // 출결 확인
    @GetMapping("/today/attendance-statistics")
    public ResponseEntity<AttendanceDetailsDTO> getTodayAttendanceStatistics() {
        AttendanceDetailsDTO statistics = attendanceService.getTodayScheduleBasedStatistics();
        return ResponseEntity.ok(statistics);
    }

}
