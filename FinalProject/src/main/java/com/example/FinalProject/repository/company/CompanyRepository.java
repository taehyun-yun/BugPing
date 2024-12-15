package com.example.FinalProject.repository.company;

import com.example.FinalProject.entity.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,String> {
    Boolean existsByCnum(String cnum);
    Boolean existsByCompanyCode(String companyCode);
    Optional<Company> findByCompanyCode(String companyCode);
}
