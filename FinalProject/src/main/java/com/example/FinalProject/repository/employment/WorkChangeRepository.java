package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface WorkChangeRepository extends JpaRepository<WorkChange, Integer> {
     @Query("SELECT wc FROM WorkChange wc " +
             "WHERE wc.schedule.scheduleId = :scheduleId " +
             "AND wc.changeDate <= :currentDate " +
             "ORDER BY wc.changeDate DESC, wc.changeStartTime DESC")
     List<WorkChange> findLatestWorkChangesByScheduleIdAndDate(@Param("scheduleId") Integer scheduleId,
                                                               @Param("currentDate") LocalDate currentDate);

     /*@Query(value = "SELECT * FROM work_change " +
             "WHERE schedule_id = :scheduleId " +
             "AND change_date BETWEEN :startDate AND :endDate " +
             "ORDER BY change_start_time DESC LIMIT 1", nativeQuery = true)
     WorkChange findLatestWorkChange(@Param("scheduleId") Integer scheduleId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
*/
     @Query("SELECT wc FROM WorkChange wc " +
             "WHERE wc.schedule.scheduleId = :scheduleId " +
             "AND wc.changeDate = :changeDate " +
             "ORDER BY wc.workChangeId DESC")
     Optional<WorkChange> findLatestWorkChange(@Param("scheduleId") Integer scheduleId,
                                                    @Param("changeDate") LocalDate changeDate);



     Optional<WorkChange> findTopByScheduleAndChangeDate(Schedule schedule, LocalDate changeDate);

//-------------------------------------------Joonho--------------------------------------------------
     //출첵용. In Out 둘다 불러감. 프런트에서 구분할 거임.
     //유저 아이디로 종료 안된 스케쥴들 불러오기 -> 계약 종료일이 내일보다 작으면 됨. 최신 날짜 순
     @Query("SELECT wc FROM WorkChange wc " +
             "WHERE wc.schedule.contract.work.user.userId = :userId " +
             "AND wc.schedule.contract.work.company.companyId = :companyId " +
             "AND wc.schedule.contract.contractEnd > :tomorrow " +
             "AND wc.schedule.contract.contractStart < :today " +
             "AND wc.schedule.contract.work.resignDate IS NULL " +
             "AND wc.schedule.contract.status = 'T' " +
             "AND wc.schedule.status = 'T' " +
             "ORDER BY wc.changeDate")
     List<WorkChange> findOneWorkChange(String userId, Integer companyId, LocalDateTime tomorrow, LocalDateTime today);
}


