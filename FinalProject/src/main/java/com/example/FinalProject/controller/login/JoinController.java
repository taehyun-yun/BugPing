package com.example.FinalProject.controller.login;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.jwt.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JoinController {
    @Autowired
    UserRepository userRepository;

    private final JoinService joinService;
    @Autowired
    JoinController(JoinService joinService){
        this.joinService = joinService;
    }

    //회원가입
    @PostMapping(value = "/userRegister")
    public String register(@RequestBody User user){
        return joinService.joinprocess(user).getBody();
    }
}
