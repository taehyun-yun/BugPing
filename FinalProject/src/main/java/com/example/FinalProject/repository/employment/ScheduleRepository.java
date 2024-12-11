package com.example.FinalProject.repository.employment;


import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    // 특정 Contract ID로 스케줄을 조회하는 메서드
    List<Schedule> findByContractContractId(Integer contractId);


    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "WHERE s.id = :scheduleId")
    Schedule findScheduleWithContractWorkAndUser(@Param("scheduleId") Long scheduleId);

    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u")
    List<Schedule> findAllSchedulesWithContractWorkAndUser();
}
