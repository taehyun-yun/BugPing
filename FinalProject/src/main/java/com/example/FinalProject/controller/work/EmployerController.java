package com.example.FinalProject.controller.work;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.employment.ContractRepository;
import com.example.FinalProject.repository.employment.ScheduleRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import com.example.FinalProject.service.jwt.JoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/employer")
public class EmployerController {
    final private JoinService joinService;
    final private UserRepository userRepository;
    final private WorkRepository workRepository;
    final private ScheduleRepository scheduleRepository;

    EmployerController(JoinService joinService, UserRepository userRepository, WorkRepository workRepository, ScheduleRepository scheduleRepository){
        this.joinService = joinService;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
        this.scheduleRepository = scheduleRepository;
    }
    @GetMapping("/findOwnCompany")
    public ResponseEntity<Map<String,Object>> findOwnCompany(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<List<Work>> worksEmployer = workRepository.findByUser_userIdAndUser_Role(authentication.getName(),"employer");
        if(worksEmployer.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Map<String,Object> response = new HashMap<>();
        List<Company>companies = new LinkedList<>();
        worksEmployer.get().forEach(work -> companies.add(work.getCompany()));
        response.put("companies", companies );
        response.put("works",workRepository.findMyCompaniesWorkers(companies));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/resignWorker")
    public ResponseEntity<String> resignWorker(@RequestParam Integer workId){
        //퇴사는 민감하니까 아이디가 사장인지 확인하자
        Optional<Work> work = workRepository.findById(workId);
        if(work.isEmpty()){
            return new ResponseEntity<>("잘못된 접근입니다.",HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //로그인한 아이디와 workId로 찾은 work의 userId 비교
        if(work.get().getUser().getUserId().equals(authentication.getName())){
            return new ResponseEntity<>("넌 사장이 아닌데 여기 어떻게 왔음?",HttpStatus.OK);
        }
        Work resign = work.get();
        resign.setResignDate(LocalDate.now());
        workRepository.save(resign);
        return new ResponseEntity<>("퇴사처리되었습니다.",HttpStatus.OK);
    }
    @PostMapping("/companyRegister")
    public ResponseEntity<String> companyRegister(@ModelAttribute Company company){
        if(!joinService.checkCompany(company)){
            return new ResponseEntity<>("이미 등록된 사업자 등록번호입니다.",HttpStatus.NOT_ACCEPTABLE);
        };
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if(userId.equals("anonymousUser")){
            return new ResponseEntity<>("로그인이 되지 않았습니다. 어떻게 오셨습니까...",HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return new ResponseEntity<>("등록되지 않은 아이디입니다. 누구십니까...", HttpStatus.NOT_ACCEPTABLE);
        }
        Company newCompany = joinService.registCompany(company);
        joinService.registWork(user.get(),newCompany);
        return new ResponseEntity<>("등록되었습니다.",HttpStatus.CREATED);
    }
}
