package com.example.FinalProject.controller.login;

import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.jwt.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private final UserValidationService userValidationService;
    public UserController(UserValidationService userValidationService){
        this.userValidationService = userValidationService;
    }

    //이메일확인
    @PostMapping("/sendCode")
    public ResponseEntity<String> sendCode(@RequestBody Map<String,String> map){
        System.out.println(map.get("userEmail"));
        return userValidationService.sendCode("junho9661@gmail.com");
    }
}
