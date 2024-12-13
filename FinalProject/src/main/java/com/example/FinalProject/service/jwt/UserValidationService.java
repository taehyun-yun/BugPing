package com.example.FinalProject.service.jwt;

import com.example.FinalProject.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService {
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    UserValidationService(PasswordEncoder passwordEncoder, UserRepository userRepository){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    //이메일 확인
    //코드전송
    public ResponseEntity<String> sendcode (){
        return new ResponseEntity<>("괜찮쓰",HttpStatus.OK);
    }
    //아이디 찾기
    //비밀번호 재설정
}
