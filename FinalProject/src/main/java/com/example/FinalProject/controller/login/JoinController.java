package com.example.FinalProject.controller.login;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.service.jwt.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JoinController {

    private final JoinService joinService;
    @Autowired
    JoinController(JoinService joinService){
        this.joinService = joinService;
    }

    //회원가입
    @PostMapping(value = "/userRegister")
    public ResponseEntity<String> register(@ModelAttribute User user, @ModelAttribute Company company){
        User newUser = joinService.joinprocess(user);
        if(newUser != null) {
            if(company.getCname()!=null){
                joinService.registCompany(company);
            }
            return new ResponseEntity<>("회원가입 성공",HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("회원가입 실패",HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
