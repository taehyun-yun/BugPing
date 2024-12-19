package com.example.FinalProject.service.jwt;

import com.example.FinalProject.dto.UserFindIdDTO;
import com.example.FinalProject.entity.user.EmailCheck;
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
import java.util.*;

@Service
public class EmailService {
    @Autowired
    private final JavaMailSender javaMailSender;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EmailRepository emailRepository;


    String letter = "abcdeftghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

    EmailService(JavaMailSender javaMailSender, PasswordEncoder passwordEncoder, UserRepository userRepository, EmailRepository emailRepository){
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }
    public String createCode (int letterlength){
        //인증코드 생성
        List<String>codenumbers = Arrays.asList(letter.split(""));
        Collections.shuffle(codenumbers);
        String code = "";
        for(int i = 0 ; i < letterlength ; i++){
            code += codenumbers.get(i);
        }
        return code;
    }

    //코드전송
    public ResponseEntity<String> sendCode (String inputEmail){
        String code = createCode(5);
        //테이블 삭제후 저장
        Optional<EmailCheck> exist = emailRepository.findByEmail(inputEmail);
        if(exist.isPresent()){
            emailRepository.delete(exist.get());
        }
        EmailCheck email = new EmailCheck();
        email.setEmail(inputEmail);
        email.setCode(code);
        email.setDue(LocalDateTime.now().plusMinutes(5L));
        emailRepository.save(email);
        //문자전송
        try{
            SimpleMailMessage smm = new SimpleMailMessage();
            smm.setTo(inputEmail);
            smm.setFrom("MasterOfManagement@gmail.com");
            smm.setSubject("[운영의 달인] 본인인증 문자입니다.");
            smm.setText("운영의 달인 본인인증 문자입니다. 최대 5분간 유효합니다.\n"+code);
            javaMailSender.send(smm);
            return new ResponseEntity<>("전송되었습니다.",HttpStatus.OK);
        } catch (org.springframework.mail.MailSendException e) {
            emailRepository.delete(email);
            return new ResponseEntity<>("이메일 전송이 실패하였습니다.",HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            emailRepository.delete(email);
            return new ResponseEntity<>("오류가 발생하였습니다.",HttpStatus.BAD_REQUEST);
        }
    }
    //이메일 확인
    public boolean isValidCode(String inputEmail, String code){
        Optional<EmailCheck> exist = emailRepository.findByEmail(inputEmail);
        Map<String, Object> map = new HashMap<>();
        if(exist.isPresent()){
            EmailCheck emailCheck = exist.get();
            //인증코드는 1회용이므로 삭제
            emailRepository.delete(emailCheck);
            if(emailCheck.getDue().isAfter(LocalDateTime.now()) && emailCheck.getCode().equals(code)){
                return true;
            }
        }
        return false;
    }
    //이메일에 등록된 아이디 목록 조회
    public List<UserFindIdDTO>findId(String inputEmail){
        Optional<List<UserFindIdDTO>> existlist = userRepository.findUserIdAndRegDateByEmail(inputEmail);
        if(existlist.isPresent()){
            return  existlist.get();
        }
        else return null;
    }
    //비밀번호 재설정
    public String newPassword(String userId,String newPassword){
        User user = userRepository.findById(userId).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return newPassword;
    }
}
