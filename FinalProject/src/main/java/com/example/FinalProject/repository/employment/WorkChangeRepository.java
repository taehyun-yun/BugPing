package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.WorkChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface WorkChangeRepository extends JpaRepository<WorkChange, Integer> {

     // scheduleId와 changeDate로 WorkChange 목록 조회
     List<WorkChange> findBySchedule_ScheduleIdAndChangeDate(Integer scheduleId, LocalDate changeDate);


     // 특정 스케줄 ID 목록과 날짜 범위에 해당하는 모든 WorkChange 조회
     @Query("SELECT wc FROM WorkChange wc WHERE wc.schedule.scheduleId IN :scheduleIds AND wc.changeDate BETWEEN :startDate AND :endDate")
     List<WorkChange> findAllByScheduleIdsAndDateRange(@Param("scheduleIds") List<Integer> scheduleIds,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

}



