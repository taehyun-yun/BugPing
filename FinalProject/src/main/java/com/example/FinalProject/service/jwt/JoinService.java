package com.example.FinalProject.service.jwt;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class JoinService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Autowired
    public JoinService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    //회원가입 프로세스
    public ResponseEntity<String>joinprocess(User user){
        String msg;
        if(check(user)){
            signup(user);
            return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("회원가입 실패", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    //아이디 중복 확인
    public Boolean check (User user){
        Boolean result = true;
        if(userRepository.existsByUserId(user.getUserId())){
            result = false;
        }
        return result;
    }

    //회원가입
    public User signup(User user){
        String encodepw = passwordEncoder.encode(user.getPassword());
        User signupUser = User.builder()
                .userId(user.getUserId())
                .password(encodepw)
                .name(user.getName())
                .tel(user.getTel())
                .birth(user.getBirth())
                .gender(user.getGender())
                .role(user.getRole())
                .regDate(LocalDate.now())
                .build();
        userRepository.save(signupUser);
        return signupUser;
    }
}
