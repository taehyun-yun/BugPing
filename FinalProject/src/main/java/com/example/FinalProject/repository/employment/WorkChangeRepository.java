package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.WorkChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface WorkChangeRepository extends JpaRepository<WorkChange, Integer> {

     // scheduleId와 changeDate로 WorkChange 목록 조회
     List<WorkChange> findBySchedule_ScheduleIdAndChangeDate(Integer scheduleId, LocalDate changeDate);


     // 특정 스케줄 ID 목록과 날짜 범위에 해당하는 모든 WorkChange 조회
     @Query("SELECT wc FROM WorkChange wc WHERE wc.schedule.scheduleId IN :scheduleIds AND wc.changeDate BETWEEN :startDate AND :endDate")
     List<WorkChange> findAllByScheduleIdsAndDateRange(@Param("scheduleIds") List<Integer> scheduleIds,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

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


    //----------------------------------------ES------------------------------------------------
    // 특정 스케줄에 대한 WorkChange 데이터가 존재하는지 확인
    boolean existsBySchedule_ScheduleId(Integer scheduleId);
}



