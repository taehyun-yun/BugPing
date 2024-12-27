package com.example.FinalProject.repository.company;

import com.example.FinalProject.entity.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Integer> {
    Boolean existsByCnum(String cnum);

    Boolean existsByCompanyCode(String companyCode);
    Optional<Company> findByCompanyCode(String companyCode);
}
