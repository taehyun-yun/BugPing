package com.example.FinalProject.repository.employment;


import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    //----------------------------------- JH ---------------------------------

    // userId의 모든 일정
    List<Schedule> findByContract_Work_User_UserId(String userId);

    // 날짜 범위로 스케줄 검색
    List<Schedule> findByContract_ContractStartBetween(LocalDate start, LocalDate end);


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
}
