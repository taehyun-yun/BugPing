package com.example.FinalProject.service.jwt;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService {
    //주의점
    //secretkey는 토큰 생성을 위한 비밀키이지, 비밀번호를 생성하기 위한 비밀 코드가 아님.
    //passwordEncoder는 아이디의 유효성을 검사하는데 쓰이고,
    //유효성을 검사하고 난 이후에 토큰을 생성하는 순서라,
    //passwordEncoder는 jwt토큰을 생성하는 것과는 상관없다.
    private final String secretkey = "dfadfksfjadfaflsdfnklbnlknklfdfdlff123213123";
    byte[] secretKeyBytes = secretkey.getBytes(StandardCharsets.UTF_8);


    //토큰을 생성하는 매서드
    @Override
    public String getToken(String userId, String role){
        //유효시간
        Date expiredate = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000); // 7일
        //키의 설정- 타입은 jwt, 알고리즘은 hs256으로 하겠다 선언
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ","JWT");
        headerMap.put("alg","HS256");
        //payload : json형식으로 토큰에 저장할 정보를 넣는거임
        Map<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("role",role);
        //JWT 토큰 생성기
        JwtBuilder builder = Jwts.builder()
                .setHeader(headerMap)
                .setClaims(map)
                .setExpiration(expiredate)
                .signWith(SignatureAlgorithm.HS256,secretKeyBytes);
        //최종 직렬화를 리턴, 위에서 설정한 헤더, 클레임, 서명 등의 정보들을 바탕으로 JWT 토큰을 문자열로 생성.
        return builder.compact();
    }
    //payload에 저장된 값(주로 아이디) 가져오기
    @Override
    public Claims getClaims(String token){
        if (token != null && !token.isEmpty()){
            return Jwts.parserBuilder()
                    .setSigningKey(secretKeyBytes)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        return null;
    }
    @Override
    public boolean validateToken(String token){
        try{
            Claims claims = getClaims(token);
            return claims != null && claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
    @Override
    public Authentication getAuthentication(String token){
        try {
            Claims claims = getClaims(token);
            if (claims == null) {
                return null;
            }
            String userId = claims.get("userId").toString();
            String role = claims.get("role").toString();
            String[] roles = role.split(",");

            List<GrantedAuthority>authorities = new ArrayList<>();
            for (String r : roles) {
                //authentication.getAuthorities()했을 때 ROLE_employee로 나온다.
                //config에서 .requestMatchers("/api/**").hasAnyRole("employee") 이렇게 'ROLE_' 이 없어도
                //String Security에서 ROLE_ 을 생각하기 때문에 ROLE_ 붙이는 작업을 해야한다.
                //System.out.println("추가된 authority : " + "ROLE_" + r.trim());
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.trim()));
            }

            return new UsernamePasswordAuthenticationToken(userId,"",authorities);
        } catch (JwtException e) {
            return null;
        }
    }

    // 로그인 사용자 ID 추출 메서드
    public String getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // 로그인된 사용자의 ID 반환
        }
        return null; // 인증되지 않은 경우
    }

}
