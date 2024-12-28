package com.example.FinalProject.repository.employment;


import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    //----------------------------------- JH ---------------------------------
    //UserId로 조회
    List<Schedule> findByContract_Work_User_UserId(String userId);

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.contract.work.company.companyId = :companyId")
    List<Schedule> findByCompanyId(@Param("companyId") Integer companyId);

    List<Schedule> findByContract_Work_Company_CompanyId(Integer companyId);

    //----------------------------------- ES ---------------------------------
    // 특정 Contract ID로 스케줄을 조회하는 메서드
    List<Schedule> findByContractContractId(Integer contractId);

    // 특정 계약 ID에 연결된 스케줄이 존재하는지 확인 //계약 삭제시 사용 (T,F 둘다 조회)
    boolean existsByContract_ContractId(Integer contractId);

    //특정 Contract ID로 스케줄을 조회 + 패치조인
    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "WHERE c.id = :contractId AND s.status = 'T'")
    List<Schedule> findSchedulesByContractIdWithContractWorkAndUser(@Param("contractId") Integer contractId);


    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "WHERE s.id = :scheduleId AND s.status = 'T'")
    Schedule findScheduleWithContractWorkAndUser(@Param("scheduleId") Integer scheduleId);

    @Query("SELECT DISTINCT s FROM Schedule s " +
            "JOIN FETCH s.contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "WHERE s.status = 'T'")
    List<Schedule> findAllSchedulesWithContractWorkAndUser();

// ====================================== TH =============================================================
    @Query("SELECT s FROM Schedule s WHERE s.contract.work.company.companyId = :companyId AND s.day = :dayOfWeek")
    List<Schedule> findSchedulesByCompanyAndDay(@Param("companyId") Integer companyId, @Param("dayOfWeek") int dayOfWeek);
//=====================================Joonho============================================================
    //출첵용. 유저 아이디로 종료 안된 스케쥴들 불러오기 -> 계약 종료일이 내일보다 작으면 됨. 최신 근무지순, 최신 계약 순, 요일 순 정렬
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.contract.work.user.userId = :userId" +
            " And s.contract.work.company.companyId = :companyId" +
            " AND s.contract.work.resignDate IS NULL" +
            " AND s.contract.status = 'T'" +
            " AND s.status = 'T'" +
            " AND s.contract.contractEnd > :tomorrow" +
            " AND s.contract.contractStart <= :today" +
            " ORDER BY s.contract.work.workId DESC, s.contract.contractId DESC, s.day ASC")
    List<Schedule> findOneSchedules(String userId, Integer companyId,LocalDateTime tomorrow, LocalDateTime today);
//    @Query("SELECT s, a FROM Schedule s " +
//            "LEFT JOIN Attendance a ON a.schedule = s " +
//            "WHERE s.day = :dayOfWeek")
//    List<Object[]> findSchedulesWithAttendances(@Param("dayOfWeek") Integer dayOfWeek);
}
