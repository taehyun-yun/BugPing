package com.example.FinalProject.repository.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.FinalProject.entity.attendance.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository  extends JpaRepository<Attendance, Integer> {
    //-------------------------------------------------------------- TH --------------------------------------------------------------
    @Query("SELECT a FROM Attendance a " +
            "LEFT JOIN a.schedule s " +
            "WHERE a.actualStart >= :startDate " +
            "AND a.actualEnd <= :endDate")
    List<Attendance> findAttendancesByDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Work ID와 날짜 범위로 Attendance 조회
    @Query("SELECT a FROM Attendance a " +
            "JOIN a.schedule s " +
            "JOIN s.contract c " +
            "WHERE c.work.workId = :workId " +
            "AND a.actualStart BETWEEN :startDate AND :endDate")
    List<Attendance> findAttendancesByWorkIdAndDateRange(
            @Param("workId") Integer workId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Attendance a " +
            "JOIN a.schedule s " +
            "JOIN s.contract c " +
            "JOIN c.work w " +
            "WHERE w.user.userId = :userId " +
            "AND w.workId = :workId")
    List<Attendance> findAttendancesByUserIdAndWorkId(@Param("userId") String userId, @Param("workId") Integer workId);

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.schedule.contract.work.user.userId = :userId " +
            "AND a.actualStart >= :startDate AND a.actualEnd <= :endDate")
    List<Attendance> findAttendancesByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    //-------------------------------------------------------------- ES --------------------------------------------------------------
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

    // 금일 출근자 조회
    @Query("SELECT DISTINCT a FROM Attendance a " +
            "JOIN FETCH a.schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "WHERE a.actualStart BETWEEN :startOfDay AND :endOfDay")
    List<Attendance> findTodayAttendances(@Param("startOfDay") LocalDateTime startOfDay,
                                         @Param("endOfDay") LocalDateTime endOfDay);
}
