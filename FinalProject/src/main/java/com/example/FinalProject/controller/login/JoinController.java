package com.example.FinalProject.controller.login;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println(user);
        //가입시 중복 아이디 체크
        if (joinService.check(user)){
            return "good";
        }
        //String response = joinService.joinprocess(user).getBody();
        return "bad";
    }
}
