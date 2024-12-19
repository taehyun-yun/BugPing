package com.example.FinalProject.controller.login;

import com.example.FinalProject.dto.UserFindIdDTO;
import com.example.FinalProject.service.jwt.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class EmailController {

    @Autowired
    private final EmailService emailService;
    public EmailController(EmailService emailService){
        this.emailService = emailService;
    }

    //이메일 전송
    @PostMapping("/sendCode")
    public ResponseEntity<String>sendCode(@RequestBody Map<String,String> map){
        return emailService.sendCode(map.get("inputEmail"));
    }
    //인증번호 확인
    @PostMapping("/checkCode")
    public Boolean checkcode(@RequestBody Map<String,String> map){
        String inputEmail = map.get("inputEmail");
        String inputCode = map.get("inputCode");
        return emailService.isValidCode(inputEmail,inputCode);
    }
    @PostMapping("/findId")
    public List<UserFindIdDTO> findId(@RequestBody Map<String,String>map){
        String inputEmail = map.get("inputEmail");
        return emailService.findId(inputEmail);
    }
    @PostMapping("/setNewPassword")
    public ResponseEntity<String> setNewPassword(@RequestBody Map<String,String>map){
        String userId = map.get("userId");
        String newPassword = map.get("newPassword");
        emailService.newPassword(userId,newPassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
