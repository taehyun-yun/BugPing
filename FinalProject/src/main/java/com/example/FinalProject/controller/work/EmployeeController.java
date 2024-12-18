package com.example.FinalProject.controller.work;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ContractRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.employment.ContractService;
import com.example.FinalProject.service.jwt.JoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    final private JoinService joinService;
    final private UserRepository userRepository;
    final private CompanyRepository companyRepository;
    final private WorkRepository workRepository;
    final private ContractRepository contractRepository;

    EmployeeController(JoinService joinService, UserRepository userRepository, CompanyRepository companyRepository, WorkRepository workRepository, ContractRepository contractRepository, ContractService contractService){
        this.joinService = joinService;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
        this.companyRepository = companyRepository;
        this.contractRepository = contractRepository;
    }

    @GetMapping("/getCompanyInfo")
    public ResponseEntity<Company> getCompanyInfo (@RequestParam String code){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Company> company = companyRepository.findByCompanyCode(code);
        if(company.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(company.get(),HttpStatus.OK);
    }
    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(@RequestBody Map<String,Integer> map){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findById(authentication.getName());
        Optional<Company> company = companyRepository.findById(map.get("companyId"));
        if(user.isPresent()&&company.isPresent()){
            if(joinService.notWorkingHere(user.get(),company.get())){
                joinService.registWork(user.get(),company.get());
                return new ResponseEntity<>("등록되었습니다.",HttpStatus.OK);
            }
            return new ResponseEntity<>("이미 등록된 아이디 입니다. 퇴사 처리 후 다시 신청할 수 있습니다.",HttpStatus.ALREADY_REPORTED);
        }
        return new ResponseEntity<>("비정상적인 접근입니다.",HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/getMyAllContract")
    public ResponseEntity<Map<String,Object>>myAllContract(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Optional<List<Work>>work = workRepository.findByUser_userIdOrderByHireDateDesc(userId);
        Optional<List<Contract>>contract = contractRepository.findAllContractsByUserId(userId);
        Map<String,Object> response = new HashMap<>();
        if(work.isPresent()){
            response.put("work",work.get());
        }
        if(contract.isPresent()){
            response.put("contract",contract.get());
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
