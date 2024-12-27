package com.example.FinalProject.controller.login;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.repository.user.UserRepository;
import com.example.FinalProject.service.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
    public ResponseEntity<Map<String,Object>> login(@RequestBody Map<String,String> loginDetails, HttpServletResponse response){
        String userId = loginDetails.get("userId");
        String password = loginDetails.get("password");
        Map<String,Object> map = new HashMap<>();
            Optional<User>existuser = userRepository.findById(userId);
            if(existuser.isEmpty()){
                map.put("msg","해당 아이디는 존재하지 않습니다.");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }
            User user = existuser.get();
            if(!passwordEncoder.matches(password,user.getPassword())){
                map.put("msg","비밀번호가 틀렸습니다.");
                return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
            }
            //2 토큰 문자열 생성
            String token = jwtService.getToken(user.getUserId(),user.getRole());
            //httpOnly 토큰 생성
            setTokenAsHttpOnlyCookies(token,response);
            //return 역할. axios에서 이 값을 가지고 어떤 페이지로 이동할지 정하면 됨.
            map.put("userId", userId);
            map.put("email",user.getEmail());
            map.put("roles",user.getRole());
            return new ResponseEntity<>(map,HttpStatus.OK);
    }
    //httpOnly 토큰 생성하기
    private void setTokenAsHttpOnlyCookies(String token, HttpServletResponse response){
        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setHttpOnly(true);//HttpOnly설정
        //Https 사용시에만 전송되도록하게하는 건 따로 프론트에서 설정하는게 있음.
        //cookie.setSecure(true);
        cookie.setSecure(false);
        cookie.setPath("/");//애플리케이션 전체에서 쿠키 사용가능
        cookie.setMaxAge( 7 * 24 * 60 * 60);// 7일
        response.addCookie(cookie);
        //System.out.println("쿠키 생성됨");
    }

    //토큰에서 아이디 가지고오기
    // 근데 필터에서 authentication을 선언해주었기 때문에 authentication.getName() 이거 쓰면 이 매서드쓸 필요 없음.
    // --시작-->
    @GetMapping("/checkuserid")
    public ResponseEntity<String>checkuserid(String token){
        Claims claims = jwtService.getClaims(token);
        if(claims != null){
            return new ResponseEntity<String>("토큰이 유효하지 않습니다.",HttpStatus.NOT_ACCEPTABLE);
        }
        String userid = claims.get("userId").toString();
        Optional<User> user = userRepository.findById(userid);
        if (user.isEmpty()){
            return new ResponseEntity<String>("유저를 찾을 수 없습니다",HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<String>(user.get().getUserId(),HttpStatus.OK);
        }
    }
    // <--토큰에서 아이디 가지고 오기 끝--

    //HttpOnly라 클라이언트에서 getCookie를 못하여 서버에서 해독해서 보내줘야함.
    @GetMapping("/findrole")
    public Map<String, Object> findRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 권한(Role) 이름만 추출
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.add(authority.getAuthority());
        }
        // 명시적으로 JSON 구조화
        Map<String, Object> response = new HashMap<>();
        response.put("roles", roles);
        return response;
    }
    //특정 쿠키 가져오기
    //jwtToken으로 만든 쿠키의 정보가 필요하면 필터에서 매번 authentic을 선언해주니까 상관 없지만,
    //로그아웃을 하거나, 그 이외의 특정 쿠키를 가지고 오고 싶을 때 사용한다.
    public Map<String, Object> findRole(String cookieName,HttpServletRequest request) {
        String token = null;
        Map<String, Object> response = new HashMap<>();
        for(Cookie cookie:request.getCookies()){
            if(cookieName.equals(cookie.getName())){
                response.put(cookieName,cookie);
                response.put("token" ,cookie.getValue());
            }
        }
        return response;
    }
    //로그아웃
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response, HttpServletRequest request){
        Map<String,Object> map = findRole("jwtToken",request);
        Cookie cookie = (Cookie) map.get("jwtToken");
        if(cookie != null){
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/isLoggedIn")
    public ResponseEntity<Boolean> isLoggIn(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(userRepository.findById(authentication.getName()).isPresent(),HttpStatus.OK);
    }
}
