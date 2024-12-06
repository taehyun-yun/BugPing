package com.example.FinalProject.repository.commute;

import com.example.FinalProject.entity.commute.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    // 특정 계약 ID에 연관된 출석 정보를 가져옵니다
    // List<Attendance> findByContractId(Integer contractId);

    // 특정 계약 ID와 스케줄 ID에 연관된 출석 정보를 가져옵니다
    List<Attendance> findByContractIdAndScheduleId(Integer contractId, Integer scheduleId);

}
