package com.example.FinalProject.controller.login;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "/register")
    public String register(@ModelAttribute User user){
        String response = joinService.joinprocess(user).getBody();
        return "good";
    }
}
