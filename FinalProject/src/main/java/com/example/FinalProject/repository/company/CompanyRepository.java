package com.example.FinalProject.repository.company;

import com.example.FinalProject.entity.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,String> {
    Boolean existsByCnum(String cnum);

    @Query("SELECT DISTINCT w.company FROM Work w WHERE w.user.userId = :userId")
    Company findByUserId(@Param("userId") String userId);
}
