package com.example.FinalProject.service.jwt;

import com.example.FinalProject.entity.user.Email;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.user.EmailRepository;
import com.example.FinalProject.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserValidationService {
    @Autowired
    private final JavaMailSender javaMailSender;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EmailRepository emailRepository;


    String letter = "abcdeftghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

    UserValidationService(JavaMailSender javaMailSender, PasswordEncoder passwordEncoder, UserRepository userRepository, EmailRepository emailRepository){
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }
    //이메일 확인
    //코드전송
    public ResponseEntity<String> sendCode (String userEmail){
        //인증코드 생성
        List<String>codenumbers = Arrays.asList(letter.split(""));
        Collections.shuffle(codenumbers);
        String code = "";
        for(int i = 0 ; i < 5; i++){
            code += codenumbers.get(i);
        }
        //테이블 저장
        Optional<Email> exist = emailRepository.findByUser_userId(userEmail);
        if(exist.isPresent()){
            emailRepository.delete(exist.get());
        }

        User user = new User();
        user.setUserId(userEmail);
        Email email = new Email();
        email.setUser(user);
        email.setCode(code);
        email.setDueTime(LocalDateTime.now().plusMinutes(5L));
        emailRepository.save(email);
        //문자전송
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(userEmail);
        smm.setFrom("MasterOfManagement@gmail.com");
        smm.setSubject("[운영의 달인] 본인인증 문자입니다.");
        smm.setText("운영의 달인 본인인증 문자입니다. 최대 5분간 유효합니다.\n"+code);
        javaMailSender.send(smm);
        return new ResponseEntity<>("전송되었습니다.",HttpStatus.OK);
    }
    //아이디 찾기
    //비밀번호 재설정
}
