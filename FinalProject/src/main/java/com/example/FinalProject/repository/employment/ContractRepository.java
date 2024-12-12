package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

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
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT c FROM Contract c WHERE c.work.workId = :workId")
    Contract findByWorkId(@Param("workId") Integer workId);


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
}