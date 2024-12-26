package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface WorkChangeRepository extends JpaRepository<WorkChange, Integer> {


     //특정 스케줄, 날짜, IN/OUT 상태의 WorkChange 조회
     @Query("SELECT wc FROM WorkChange wc WHERE wc.schedule.scheduleId = :scheduleId AND wc.changeDate = :changeDate AND wc.inOut = :inOut")
     Optional<WorkChange> findBySchedule_ScheduleIdAndChangeDateAndInOut(
             Integer scheduleId, LocalDate changeDate, String inOut);



     //특정 스케줄과 날짜의 모든 WorkChange 조회
     @Query("SELECT wc FROM WorkChange wc WHERE wc.schedule.scheduleId = :scheduleId AND wc.changeDate = :changeDate")
     List<WorkChange> findByScheduleIdAndChangeDate(@Param("scheduleId") Integer scheduleId, @Param("changeDate") LocalDate changeDate);

     //최신 WorkChange 조회 - 필요 시 활용
     @Query("SELECT wc FROM WorkChange wc WHERE wc.schedule.scheduleId = :scheduleId " +
             "AND wc.changeDate = :changeDate " +
             "ORDER BY wc.workChangeId DESC")
     Optional<WorkChange> findLatestWorkChange(@Param("scheduleId") Integer scheduleId,
                                               @Param("changeDate") LocalDate changeDate);

     @Query("SELECT wc FROM WorkChange wc WHERE wc.schedule.scheduleId IN :scheduleIds AND wc.changeDate BETWEEN :start AND :end")
     List<WorkChange> findAllByScheduleIdsAndDateRange(
             @Param("scheduleIds") List<Integer> scheduleIds,
             @Param("start") LocalDate start,
             @Param("end") LocalDate end);

}


