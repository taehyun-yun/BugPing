package com.example.FinalProject.service.jwt;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.company.CompanyRepository;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.repository.work.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

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
    //회사코드 생성시 사용할 알파벳
    private final char[] customAlphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    
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
    //회사 코드 생성
    public String createCode (){
        String code;
        do {
           code = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR,customAlphabet,21);
        } while (companyRepository.existsByCompanyCode(code));
        return code;
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
                .companyCode(createCode())
                .build();
        companyRepository.save(newCompany);
        return newCompany;
    }
    //신규회사에 사장, 직원 등록
    public Work registWork(User user, Company company){
        Work newWork = Work.builder()
                .user(user)
                .company(company)
                .hireDate(LocalDate.now())
                .build();
        workRepository.save(newWork);
        return newWork;
    }
    //퇴사하지 않고 회사 재등록 방지 기능
    public boolean notWorkingHere (User user, Company company){
        return !workRepository.existsByUser_UserIdAndCompany_CompanyIdAndResignDateIsNull(user.getUserId(),company.getCompanyId());
    }

    //이메일 등록
    public boolean setEmail(String email){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User>user = userRepository.findById(authentication.getName());
        if(user.isEmpty()){
            return false;
        }
        user.get().setEmail(email);
        userRepository.save(user.get());
        return true;
    }
    //회원탈퇴(user table을 아이디를 PK로 해버려서, 사장님이 탈퇴유저를 조회하거나, 유저가 탈퇴한 근무처의 조회를 위해선, 아이디를 삭제할 수가 없다... 대신 개인정보를 암호화하자)
    private static final String quitRestoreKey = "restore";
    private static final byte[] key = quitRestoreKey.getBytes(StandardCharsets.UTF_8);
    private static final String algorithm = "AES";//암호화 알고리즘 방식 중 하나

    //암호화
//    public static String encrypt(String data) throws Exception {
//        Cipher cipher = Cipher.getInstance();
//    }


    public boolean setPersonInformatinNUll(String userId){
        Optional<User>isUser = userRepository.findById(userId);
        if(isUser.isEmpty()){
            return false;
        }
        User user = isUser.get();
        user.setBirth(null);
        user.setEmail(null);
        user.setGender(null);
        user.setRegDate(null);
        user.setTel(null);
        userRepository.save(user);
        return true;
    }

}
