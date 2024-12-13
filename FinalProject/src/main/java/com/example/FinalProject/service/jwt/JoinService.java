package com.example.FinalProject.service.jwt;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
@Transactional
@Service
public class JoinService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final WorkRepository workRepository;
    @Autowired
    public JoinService(PasswordEncoder passwordEncoder, UserRepository userRepository, CompanyRepository companyRepository, WorkRepository workRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.workRepository = workRepository;
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
                .email(user.getEmail())
                .regDate(LocalDate.now())
                .build();
        userRepository.save(signupUser);
        return signupUser;
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
        Company newCompany = Company.builder()
                .companyId(company.getCompanyId())
                .address(company.getAddress())
                .cname(company.getCname())
                .address(company.getAddress())
                .detailAddress(company.getDetailAddress())
                .ctel(company.getCtel())
                .cnum(company.getCnum())
                .build();
        companyRepository.save(newCompany);
        return newCompany;
    }
    //신규회사에 사장 등록
    public Work registWork(User user, Company company){
        Work newWork = Work.builder()
                .user(user)
                .company(company)
                .hireDate(LocalDate.now())
                .build();
        workRepository.save(newWork);
        return newWork;
    }
}
