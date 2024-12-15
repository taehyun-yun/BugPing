package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.jwt.JoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class NewWorkerController {
    final private JoinService joinService;
    final private WorkRepository workRepository;
    final private CompanyRepository companyRepository;

    NewWorkerController(JoinService joinService, WorkRepository workRepository, CompanyRepository companyRepository){
        this.joinService = joinService;
        this.workRepository = workRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/getCompanyInfo")
    public ResponseEntity<Company> getCompanyInfo (@RequestParam String code){
        Optional<Company> company = companyRepository.findByCompanyCode(code);
        if(company.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(company.get(),HttpStatus.OK);
    }
    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(@RequestParam Integer companyId){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
