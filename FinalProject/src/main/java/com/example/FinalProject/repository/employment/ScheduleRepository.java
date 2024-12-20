package com.example.FinalProject.repository.employment;


import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    //----------------------------------- JH ---------------------------------
    //UserId로 조회
    List<Schedule> findByContract_Work_User_UserId(String userId);

    // Contract와 관련된 모든 일정 조회
    List<Schedule> findByContract(Contract contract);

    @Query("SELECT s FROM Schedule s WHERE s.contract.work.company.companyId = :companyId")
    List<Schedule> findByCompanyId(@Param("companyId") Integer companyId);

    @Query("SELECT s FROM Schedule s WHERE s.contract.work.company.companyId = :companyId")
    List<Schedule> findSchedulesByCompanyId(@Param("companyId") Integer companyId);

    List<Schedule> findByContract_Work_Company_CompanyId(Integer companyId);

    //----------------------------------- ES ---------------------------------
    // 특정 Contract ID로 스케줄을 조회하는 메서드
    List<Schedule> findByContractContractId(Integer contractId);

    //특정 Contract ID로 스케줄을 조회 + 패치조인
    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "WHERE c.id = :contractId")
    List<Schedule> findSchedulesByContractIdWithContractWorkAndUser(@Param("contractId") Integer contractId);


    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "WHERE s.id = :scheduleId")
    Schedule findScheduleWithContractWorkAndUser(@Param("scheduleId") Integer scheduleId);

    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp ")
    List<Schedule> findAllSchedulesWithContractWorkAndUser();
// ====================================== TH =============================================================
//    // 금일 출근자 카운트
//    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.day = :dayOfWeek AND s.status = 'active'")
//    long countByDay(@Param("dayOfWeek") int dayOfWeek);

    @Query("SELECT s, a FROM Schedule s " +
            "LEFT JOIN Attendance a ON a.schedule = s " +
            "WHERE s.day = :dayOfWeek")
    List<Object[]> findSchedulesWithAttendances(@Param("dayOfWeek") Integer dayOfWeek);

    public interface WorkChangeRepository extends JpaRepository<WorkChange, Integer> {
        List<WorkChange> findByScheduleAndChangeDate(Schedule schedule, LocalDate changeDate);
    }

}


