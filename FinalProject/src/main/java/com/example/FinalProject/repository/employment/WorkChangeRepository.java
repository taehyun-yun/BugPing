package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.WorkChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;



public interface WorkChangeRepository extends JpaRepository<WorkChange, Integer> {
     @Query("SELECT wc FROM WorkChange wc " +
             "WHERE wc.schedule.scheduleId = :scheduleId " +
             "AND wc.changeDate <= :currentDate " +
             "ORDER BY wc.changeDate DESC, wc.changeStartTime DESC")
     List<WorkChange> findLatestWorkChangesByScheduleIdAndDate(@Param("scheduleId") Integer scheduleId,
                                                               @Param("currentDate") LocalDate currentDate);

     @Query(value = "SELECT * FROM work_change " +
             "WHERE schedule_id = :scheduleId " +
             "AND change_date BETWEEN :startDate AND :endDate " +
             "ORDER BY change_start_time DESC LIMIT 1", nativeQuery = true)
     WorkChange findLatestWorkChange(@Param("scheduleId") Integer scheduleId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);


}

