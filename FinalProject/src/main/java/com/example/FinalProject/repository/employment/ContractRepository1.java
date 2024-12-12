package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ContractRepository1 extends JpaRepository<Contract, Integer> {
    // 회사 ID로 모든 Contract 조회
    @Query("SELECT c FROM Contract c " +
            "JOIN c.work w " +
            "JOIN w.user u " +
            "JOIN Company co ON co.user.userId = u.userId " +
            "WHERE co.companyId = :companyId")
    List<Contract> findByCompanyId(@Param("companyId") String companyId);
}
