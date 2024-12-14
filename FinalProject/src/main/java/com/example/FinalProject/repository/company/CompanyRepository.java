package com.example.FinalProject.repository.company;

import com.example.FinalProject.entity.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,String> {
    Boolean existsByCnum(String cnum);

    Optional<Company> findByUserId(String userId);
}
