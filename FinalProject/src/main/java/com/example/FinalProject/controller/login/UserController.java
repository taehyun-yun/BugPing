package com.example.FinalProject.controller.login;

import com.example.FinalProject.service.jwt.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private final EmailService emailService;
    public UserController(EmailService emailService){
        this.emailService = emailService;
    }

    //이메일 전송
    @PostMapping("/sendCode")
    public ResponseEntity<String>sendCode(@RequestBody Map<String,String> map){
        return emailService.sendCode(map.get("inputEmail"));
    }
    //인증번호 확인
    @PostMapping("/checkCode")
    public ResponseEntity<String>checkcode(@RequestBody Map<String,String> map){
        String inputEmail = map.get("inputEmail");
        String inputCode = map.get("inputCode");
        if(emailService.isValidCode(inputEmail,inputCode)){
            return new ResponseEntity<>("인증번호 성공",HttpStatus.OK);
        }
        return new ResponseEntity<>("인증번호 실패",HttpStatus.NOT_ACCEPTABLE);
    }

}
