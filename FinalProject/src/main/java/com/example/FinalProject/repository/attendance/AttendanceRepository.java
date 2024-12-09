package com.example.FinalProject.repository.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.FinalProject.entity.attendance.Attendance;

import java.util.List;

public interface AttendanceRepository  extends JpaRepository<Attendance, Integer> {

    // 특정 스케줄 ID에 따른 출석 정보를 조회
    List<Attendance> findByScheduleScheduleId(Integer scheduleId);

    // 특정 WorkChange ID에 따른 출석 정보를 조회
    List<Attendance> findByWorkChangeWorkChangeId(Integer workChangeId);

}
