package com.example.FinalProject.repository.payroll;

import com.example.FinalProject.dto.EmployeeDTO;
import com.example.FinalProject.entity.payroll.PayRoll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<PayRoll, Integer> {
//    @Query("SELECT p, w, u FROM PayRoll p " +
//            "JOIN p.work w " +
//            "JOIN w.user u " +
//            "WHERE w.company.companyId = :companyId " + // 회사 ID로 필터링
//            "AND (w.resignDate IS NULL OR w.resignDate >= CURRENT_DATE)")
//    List<Object[]> findPayRollWithWorkAndUserByCompanyId(@Param("companyId") Integer companyId);
//
//
//    @Query("SELECT p FROM PayRoll p WHERE p.work.user.userId = :userId AND p.paymentDate BETWEEN :startDate AND :endDate")
//    Optional<PayRoll> findByUserIdAndDateRange(
//            @Param("userId") String userId,
//            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM PayRoll p " +
                "JOIN FETCH p.work w " +
                "JOIN FETCH w.user u " +
                "WHERE w.company.companyId = :companyId")
    List<PayRoll> findPayRollsByCompanyId(@Param("companyId") Integer companyId);

}