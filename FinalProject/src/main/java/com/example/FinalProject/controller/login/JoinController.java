package com.example.FinalProject.controller.login;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.service.jwt.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

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
//        System.out.println(user);
//        System.out.println(company);
    //회사 등록 할 때
    if(company.getCname()!=null){
        if(!joinService.check(user)){
            return new ResponseEntity<>("중복된 아이디입니다.",HttpStatus.NOT_ACCEPTABLE);
        }
        if(!joinService.checkCompany(company)){
            return new ResponseEntity<>("이미 등록된 사업자 등록번호입니다.",HttpStatus.NOT_ACCEPTABLE);
        }
        User newUser = joinService.signup(user);
        Company newCompany = joinService.registCompany(company);
        joinService.registWork(newUser,newCompany);
        return new ResponseEntity<>("등록되었습니다.",HttpStatus.OK);
    }
    //회사 등록 안할 때
    if(!joinService.check(user)){
        return new ResponseEntity<>("중복된 아이디입니다.",HttpStatus.NOT_ACCEPTABLE);
    }
    joinService.signup(user);
    return new ResponseEntity<>("등록되었습니다.",HttpStatus.OK);
    }

    @PostMapping("/setEmail")
    public ResponseEntity<String>setEmail(@RequestBody Map<String, String>map) {
        String email = map.get("email");
        if (!joinService.setEmail(email)) {
            return new ResponseEntity<>("로그인 되지 않아, 이메일 등록에 실패하였습니다. 여긴 어떻게 오셨습니까?", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("이메일 등록에 성공하였습니다.", HttpStatus.OK);
    }

}
