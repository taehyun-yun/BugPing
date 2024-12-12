package com.example.FinalProject.repository.company;

import com.example.FinalProject.entity.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyRepository extends JpaRepository<Company,String> {
    Boolean existsByCnum(String cnum);
}
