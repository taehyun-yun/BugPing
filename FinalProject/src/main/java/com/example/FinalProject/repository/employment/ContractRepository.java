package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Integer> {
//---------------------TH----------------------
    @Query("SELECT c FROM Contract c " +
            "JOIN c.work w " +
            "JOIN w.user u " +
            "WHERE u.userId = :userId " +
            "AND c.contractStart <= :endDate " +
            "AND c.contractEnd >= :startDate")
    Contract findValidContractByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c FROM Contract c " +
            "WHERE c.work.user.userId = :userId " +
            "AND c.contractStart <= :endDate " +
            "AND c.contractEnd >= :startDate")
    List<Contract> findAllValidContractsByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


//    @Query("SELECT c FROM Contract c WHERE c.work.workId = :workId")
//    Contract findByWorkId(@Param("workId") Integer workId);
@Query("SELECT c FROM Contract c WHERE c.work.workId = :workId")
List<Contract> findAllByWorkId(@Param("workId") Integer workId);



//---------------------ES----------------------

    @Query("SELECT DISTINCT c FROM Contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "WHERE c.id = :contractId")
    Contract findContractWithWorkAndUser(@Param("contractId") Integer contractId);

    @Query("SELECT DISTINCT c FROM Contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp ")
    List<Contract> findAllContractsWithWorkAndUser();


//---------------------JH----------------------

    // Work를 기준으로 Contract 조회
    List<Contract> findByWork(Work work);

//--------------------Joonho-------------------
    // 한 근무자의 모든 계약서를 최신근무지, 최신 계약순으로 조회
@Query("SELECT DISTINCT c FROM Contract c " +
        "JOIN FETCH c.work w " +
        "JOIN FETCH w.user u " +
        "JOIN FETCH w.company cp " +
        "where u.userId = :userId " +
        "ORDER BY w.hireDate, c.contractId DESC")
Optional<List<Contract>>findAllContractsByUserId(String userId);

}