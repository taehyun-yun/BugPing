package com.example.FinalProject.repository.attendance;

import com.example.FinalProject.entity.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    @Query("SELECT a FROM Attendance a " +
            "LEFT JOIN a.schedule s " +
            "WHERE a.actualStart >= :startDate " +
            "AND a.actualEnd <= :endDate")
    List<Attendance> findAttendancesByDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Attendance a " +
            "JOIN a.schedule s " +
            "JOIN s.contract c " +
            "JOIN c.work w " +
            "WHERE w.user.userId = :userId " +
            "AND w.workId = :workId")
    List<Attendance> findAttendancesByUserIdAndWorkId(@Param("userId") String userId, @Param("workId") Integer workId);

    // Work ID와 날짜 범위로 Attendance 조회
    @Query("SELECT a FROM Attendance a " +
            "JOIN a.schedule s " +
            "JOIN s.contract c " +
            "WHERE c.work.workId = :workId " +
            "AND a.actualStart BETWEEN :startDate AND :endDate")
    List<Attendance> findAttendancesByWorkIdAndDateRange(
            @Param("workId") Integer workId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}


