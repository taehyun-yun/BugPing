package com.example.FinalProject.repository.payroll;

import com.example.FinalProject.entity.payroll.PayRoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<PayRoll, Integer> {
    @Query("SELECT p, w, u FROM PayRoll p " +
            "JOIN p.work w " +
            "JOIN w.user u " +
            "WHERE w.resignDate IS NULL OR w.resignDate >= CURRENT_DATE")
    List<Object[]> findPayRollWithWorkAndUser(); // 조합된 데이터 반환

    @Query("SELECT p FROM PayRoll p WHERE p.work.user.userId = :userId AND p.paymentDate BETWEEN :startDate AND :endDate")
    Optional<PayRoll> findByUserIdAndDateRange(@Param("userId") String userId,
    @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
