package com.example.FinalProject.controller.login;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class JwtController {
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    public JwtController(JwtService jwtService, PasswordEncoder passwordEncoder){
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    UserRepository userRepository;

    //로그인 성공했을 때 토큰 값 저장하기 - 매번 axios에 헤더 넣기 귀찮으므로 httpOnly방식을 사용함.
    //1 아이디 비밀번호 체크
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String,String> loginDetails, HttpServletResponse response){
        String userId = loginDetails.get("userId");
        String password = loginDetails.get("password");
            Optional<User>existuser = userRepository.findById(userId);
            if(existuser.isEmpty()){
                return new ResponseEntity<>("해당 아이디는 존재하지 않습니다.", HttpStatus.NOT_FOUND);
            }
            User user = existuser.get();
            if(!passwordEncoder.matches(password,user.getPassword())){
                return new ResponseEntity<>("비밀번호가 틀렸습니다.",HttpStatus.BAD_REQUEST);
            }
            //2 토큰 문자열 생성
            String token = jwtService.getToken(user.getUserId(),user.getRole());
            //httpOnly 토큰 생성
            setTokenAsHttpOnlyCookies(token,response);
            //return 역할. axios에서 이 값을 가지고 어떤 페이지로 이동할지 정하면 됨.
            return new ResponseEntity<>(user.getRole(),HttpStatus.OK);
    }
    //httpOnly 토큰 생성하기
    private void setTokenAsHttpOnlyCookies(String token, HttpServletResponse response){
        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setHttpOnly(true);//HttpOnly설정
        //Https 사용시에만 전송되도록하게하는 건 따로 프론트에서 설정하는게 있음.
        //cookie.setSecure(true);
        cookie.setSecure(false);
        cookie.setPath("/");//애플리케이션 전체에서 쿠키 사용가능
        cookie.setMaxAge(3*60*60);//3시간
        response.addCookie(cookie);
        System.out.println("쿠키 생성됨");
    }

    //토큰에서 아이디 가지고오기 --시작-->
    @GetMapping("/checkuserid")
    public ResponseEntity<String>checkuserid(String token){
        Claims claims = jwtService.getClaims(token);
        if(claims != null){
            String userid = claims.get("id").toString();
            Optional<User> user = userRepository.findById(userid);
            if (user.isEmpty()){
                return new ResponseEntity<String>("유저를 찾을 수 없습니다",HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<String>(user.get().getUserId(),HttpStatus.OK);
            }
        }
        return new ResponseEntity<String>("토큰이 유효하지 않습니다.",HttpStatus.NOT_ACCEPTABLE);
    }
    // <--토큰에서 아이디 가지고 오기 끝--
}
