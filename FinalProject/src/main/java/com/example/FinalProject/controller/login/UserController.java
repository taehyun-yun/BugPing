package com.example.FinalProject.controller.login;

import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.jwt.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/sendcode")
    public ResponseEntity<String> sendcode(){
        return new ResponseEntity<String>("괜찮음", HttpStatus.OK);
    }
}
