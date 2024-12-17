package com.example.FinalProject.repository.work;

import com.example.FinalProject.entity.work.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
/**
 * WorkRepository는 Work 엔티티에 대한 CRUD 작업을 지원합니다.
 */
public interface WorkRepository extends JpaRepository<Work, Integer> {

    // 회사 ID로 모든 Contract 조회
    List<Work> findByCompany_CompanyId(Integer companyId);

    // userId로 company 조회할때 사용
    Work findByUser_UserId(String userId);

    Page<Work> findAllByCompany_CompanyId(Integer companyId, Pageable pageable);


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

}
