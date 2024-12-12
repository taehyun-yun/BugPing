package com.example.FinalProject.repository.contract;

import com.example.FinalProject.entity.employment.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
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
}
