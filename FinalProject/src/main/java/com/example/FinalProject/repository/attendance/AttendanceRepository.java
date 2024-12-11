package com.example.FinalProject.repository.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.FinalProject.entity.attendance.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository  extends JpaRepository<Attendance, Integer> {

    // 특정 스케줄 ID에 따른 출석 정보를 조회
    List<Attendance> findByScheduleScheduleId(Integer scheduleId);

    // 특정 WorkChange ID에 따른 출석 정보를 조회
    List<Attendance> findByWorkChangeWorkChangeId(Integer workChangeId);


    @Query("SELECT DISTINCT a FROM Attendance a " +
            "JOIN FETCH a.schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "WHERE a.id = :attendanceId")
    Attendance findAttendanceWithScheduleContractWorkAndUser(@Param("attendanceId") Long attendanceId);

    @Query("SELECT DISTINCT a FROM Attendance a " +
            "JOIN FETCH a.schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u")
    List<Attendance> findAllAttendancesWithScheduleContractWorkAndUser();

    @Query("SELECT DISTINCT a FROM Attendance a " +
            "JOIN FETCH a.workChange wc " + // WorkChange와 패치 조인
            "JOIN FETCH wc.schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "WHERE a.id = :attendanceId")
    Attendance findAttendanceWithWorkChangeScheduleContractWorkAndUser(@Param("attendanceId") Long attendanceId);

    @Query("SELECT DISTINCT a FROM Attendance a " +
            "JOIN FETCH a.workChange wc " + // WorkChange와 패치 조인
            "JOIN FETCH wc.schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u")
    List<Attendance> findAllAttendancesWithWorkChangeScheduleContractWorkAndUser();



    //출퇴근-체인지 엔티티 연결 끊고 스케쥴. 변경사항으로 타고 들어가게 하는 방법도 고려해볼 것
    @Query("SELECT DISTINCT a FROM Attendance a " +
            "JOIN FETCH a.schedule s " +
            //"JOIN FETCH a.workChange wc " + // WorkChange와 패치 조인은 제외
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "WHERE a.id = :attendanceId")
    Attendance findAttendanceWithAll(@Param("attendanceId") Long attendanceId);
}
