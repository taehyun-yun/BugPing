package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ContractRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.jwt.JoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployerController {
    final private JoinService joinService;
    final private UserRepository userRepository;
    final private CompanyRepository companyRepository;
    final private WorkRepository workRepository;
    final private ContractRepository contractRepository;

    EmployerController(JoinService joinService, UserRepository userRepository, CompanyRepository companyRepository, WorkRepository workRepository, ContractRepository contractRepository){
        this.joinService = joinService;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
        this.companyRepository = companyRepository;
        this.contractRepository = contractRepository;
    }
}
