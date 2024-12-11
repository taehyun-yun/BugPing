package com.example.FinalProject.service.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

public interface JwtService {
    //토큰을 생성하는 매서드
    String getToken(String userId, String role);
    //payload불러오는 매서드
    Claims getClaims(String token);
    //유효성 검사
    boolean validateToken(String token);
    //권한 부여
    Authentication getAuthentication(String token);
}