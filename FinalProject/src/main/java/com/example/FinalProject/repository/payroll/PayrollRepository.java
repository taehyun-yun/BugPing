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
    @Query("SELECT p FROM PayRoll p " +
                "JOIN FETCH p.work w " +
                "JOIN FETCH w.user u " +
                "WHERE w.company.companyId = :companyId")
    List<PayRoll> findPayRollsByCompanyId(@Param("companyId") Integer companyId);

    boolean existsByWork_WorkId(Integer workId);
}