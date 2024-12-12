//package com.example.FinalProject.repository.payroll;
//
//import com.example.FinalProject.dto.EmployeeDTO;
//import com.example.FinalProject.entity.attendance.Attendance;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Repository
//public interface EmployeeRepository extends JpaRepository<Attendance, Integer> {
//
//    @Query("SELECT new com.example.FinalProject.dto.EmployeeDTO(" +
//            "u.userId, u.name, " + // User 테이블의 userId와 name
//            "CAST(a.actualStart AS string), " + // Attendance 테이블의 actualStart
//            "c.hourlyWage, " + // Contract 테이블의 hourlyWage
//            "SUM(a.recognizedWorkMinute / 60), " + // 월 근무시간 계산
//            "COUNT(DISTINCT a.actualStart), " + // 월 근무일수
//            "(SUM(a.recognizedWorkMinute / 60) * c.hourlyWage), " + // 총 급여
//            "FALSE" + // 지급 여부
//            ") " +
//            "FROM Attendance a " +
//            "LEFT JOIN a.schedule s " +
//            "LEFT JOIN s.contract c " +
//            "LEFT JOIN c.work w " +
//            "LEFT JOIN w.user u " + // User와 Join
//            "WHERE u.userId = :userId " + // 조건: userId
//            "AND a.actualStart >= :startDate " + // 조건: 시작일
//            "AND a.actualEnd <= :endDate " + // 조건: 종료일
//            "GROUP BY u.userId, u.name, c.hourlyWage")
//    List<EmployeeDTO> findEmployeeDTOByDateRange(
//            @Param("userId") String userId,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate);
//}
//
//
