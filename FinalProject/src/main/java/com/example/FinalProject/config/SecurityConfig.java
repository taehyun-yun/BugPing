package com.example.FinalProject.config;

import com.example.FinalProject.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity//스프링 시큐리티 필터가 스프링 필터체인에 등록 됨
public class SecurityConfig {
    //비밀번호를 저장하거나 찾아서 비교할 때 BCryptPasswordEncoder로 할것이다.
    //비밀번호를 저장할 때 passwordEncoder가 솔트값을 함께 저장하고, 찾을 땐 솔트값으로 찾는다.

    //주의점
    //passwordEncoder는 아이디의 유효성을 검사하는데 쓰이고,
    //유효성을 검사하고 난 이후에 토큰을 생성하는 순서라,
    //passwordEncoder는 jwt토큰을 생성하는 것과는 상관없다.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    //test용 아이디
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("heart")
                        .password(passwordEncoder().encode("ping")) // 암호화된 비밀번호 설정
                        .roles("admin")
                        .build()
        );
    }

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) //명시적으로 cors 활성화
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth //페이지별 로그인 필수 페이지와, 아이디에 따라 접근 가능한 페이지 할당
                        .requestMatchers("/api/**").hasAnyRole("employer","employee","admin")
                        .requestMatchers("/employer/**").hasAnyRole("employer","admin")
                        .requestMatchers("/employee/**").hasAnyRole("employee","admin")
                        .anyRequest().permitAll()//로그인하지 않아도 접근 가능한 곳
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 추가
                //.formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form //로그인이 되지 않았을 때
                        .loginPage("http://localhost:5173/login").permitAll()
                        .defaultSuccessUrl("/")
                        .failureUrl("/findid")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutUrl("http://localhost:5173/logout")
                        .logoutSuccessUrl("/")
                )
                ;
    return http.build();
    }
}