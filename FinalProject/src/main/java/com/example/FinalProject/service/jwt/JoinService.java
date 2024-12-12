package com.example.FinalProject.service.jwt;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.company.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    @Autowired
    public JoinService(PasswordEncoder passwordEncoder, UserRepository userRepository, CompanyRepository companyRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }
    //회원가입 프로세스
    public User joinprocess(User user){
        String msg;
        if(check(user)){
            return signup(user);
        } else {
            return null;
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
    //회사 등록 프로세스
    public Company registCompanyProcess(Company company){
        if(checkCompany(company)){
            return  registCompany(company);
        }
        return null;
    }

    //회사 중복 확인
    public Boolean checkCompany (Company company){
        Boolean result = true;
        if(companyRepository.existsByCnum(company.getCnum())){
            result = false;
        }
        return result;
    }
    //회사 등록
    public Company registCompany(Company company){
        if(checkCompany(company)){
            return null;
        }
        Company newCompany = Company.builder()
                .companyId(company.getCompanyId())
                .cname(company.getCname())
                .address(company.getAddress())
                .ctel(company.getCtel())
                .cnum(company.getCnum())
                .build();
        companyRepository.save(newCompany);
        return newCompany;
    }
}
