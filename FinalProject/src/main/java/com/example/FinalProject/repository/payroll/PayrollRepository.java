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

//    @Query("SELECT p, w, u FROM PayRoll p " +
//            "JOIN p.work w " +
//            "JOIN w.user u " +
//            "WHERE w.company.companyId = :companyId " +
//            "AND p.paymentDate = (SELECT MAX(p2.paymentDate) " +
//            "FROM PayRoll p2 " +
//            "WHERE p2.work.user.userId = w.user.userId)")
//    List<Object[]> findPayRollWithWorkAndUserByCompanyId(@Param("companyId") Integer companyId);
@Query(value = "SELECT DISTINCT w.user_id, w.hire_date, p.payment_date, p.is_paid, p.pay_roll_id " +
        "FROM work w " +
        "JOIN pay_roll p ON w.work_id = p.work_id " +
        "WHERE w.company_id = :companyId " +
        "AND p.payment_date = ( " +
        "   SELECT MAX(p2.payment_date) " +
        "   FROM pay_roll p2 " +
        "   JOIN work w2 ON p2.work_id = w2.work_id " +
        "   WHERE w2.user_id = w.user_id " +
        ")",
        countQuery = "SELECT COUNT(DISTINCT w.user_id) " +
                "FROM work w " +
                "JOIN pay_roll p ON w.work_id = p.work_id " +
                "WHERE w.company_id = :companyId",
        nativeQuery = true)
Page<Object[]> findDistinctLatestPayrollByCompanyId(@Param("companyId") Integer companyId, Pageable pageable);





    @Query("SELECT p FROM PayRoll p WHERE p.work.user.userId = :userId AND p.paymentDate BETWEEN :startDate AND :endDate")
    Optional<PayRoll> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
