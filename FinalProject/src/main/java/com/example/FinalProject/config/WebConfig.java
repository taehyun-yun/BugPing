package com.example.FinalProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//서버와 클라이언트가 다른 포트번호를 사용할 때 CORS 에러를 잡아주기 위해 있는 클래스
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트에 대해
                .allowedOrigins(
                        "http://localhost:5173", // Vue.js 서버 주소
                        "http://192.168.5.23:5173" //ip 주소
                        //"http://localhost:7777" // gateway 주소
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    //부분 업데이트
    //PATCH는 리소스 전체를 수정하지 않고, 특정 부분만 수정할 때 사용됩니다.
    //예: 데이터베이스에서 특정 컬럼 하나만 업데이트하는 경우.

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/notice/files/**" URL 패턴을 "C:/uploads/" 디렉토리에 매핑
        registry.addResourceHandler("/notice/files/**")
                .addResourceLocations("file:///C:/education/KOSA_finalProject/BugPing/FinalProject/frontend/src/uploads");
    }
}