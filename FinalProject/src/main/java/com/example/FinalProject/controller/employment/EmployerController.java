package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
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

import java.util.*;

@RestController
@RequestMapping("/employer")
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
    @GetMapping("/findOwnCompany")
    public ResponseEntity<Map<String,Object>> findOwnCompany(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<List<Work>> works = workRepository.findByUser_userIdAndUser_Role(authentication.getName(),"employer");
        if(works.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Company>companies = new LinkedList<>();
        works.get().forEach(work -> companies.add(work.getCompany()));
        Map<String,Object> response = new HashMap<>();
        response.put("companies", companies );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
