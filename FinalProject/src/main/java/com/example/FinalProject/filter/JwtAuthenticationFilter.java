package com.example.FinalProject.filter;

import com.example.FinalProject.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private final JwtService jwtService;
    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 쿠키에서 JWT 토큰 추출
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            System.out.println("쿠키에서 전달된 쿠키 : "+token);
            if(jwtService.validateToken(token)) {
                //토큰이 유효하다면 Authentication 객체를 생성하고 SecurityContext에 설정
                Authentication authentication = jwtService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("토큰 유효성 검사 성공, 인증 객체 생성");
            } else{
                System.out.println("토큰 유효성 검사 실패");
            }
        } else {
            System.out.println("토큰이 쿠키에서 전달되지 않음");
        }

        // 필터 체인 진행
        filterChain.doFilter(request, response);
    }
}
