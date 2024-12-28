package com.example.FinalProject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class TestController {
    @GetMapping("/authtest")
    public Map<String,Object> authtest(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());//로그인 아이디
        System.out.println(authentication.getAuthorities());//[Role_역할
        System.out.println(authentication.getCredentials());//null
        System.out.println(authentication.getDetails());//WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=null]
        System.out.println(authentication.getPrincipal());//org.springframework.security.core.userdetails.User [Username=heart, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, CredentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_admin]]
        System.out.println(authentication.getClass());//class org.springframework.security.authentication.UsernamePasswordAuthenticationToken

        Map<String, Object> map = new HashMap<>();
        map.put("authentication.getName()",authentication.getName());
        map.put("authentication.getAuthorities()",authentication.getAuthorities());
        map.put("authentication.getCredentials()",authentication.getCredentials());
        map.put("authentication.getDetails()",authentication.getDetails());
        map.put("authentication.getPrincipal()",authentication.getPrincipal());
        map.put("authentication.getClass()",authentication.getClass());

        return map;
    }
    @GetMapping("/api/authtest")
    public Map<String,Object> apiauthtest(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());//로그인 아이디
        System.out.println(authentication.getAuthorities());//[Role_역할]
        System.out.println(authentication.getCredentials());//null
        System.out.println(authentication.getDetails());//WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=null]
        System.out.println(authentication.getPrincipal());//org.springframework.security.core.userdetails.User [Username=heart, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, CredentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_admin]]
        System.out.println(authentication.getClass());//class org.springframework.security.authentication.UsernamePasswordAuthenticationToken

        Map<String, Object> map = new HashMap<>();
        map.put("getName",authentication.getName());
        map.put("getAuthorities",authentication.getAuthorities());
        map.put("getCredentials",authentication.getCredentials());
        map.put("getDetails",authentication.getDetails());
        map.put("getPrincipal",authentication.getPrincipal());
        map.put("getClass",authentication.getClass());

        return map;
    }
    @GetMapping("/employee/sayhi")
    public String sayhi(){
        System.out.println("hi");
        return "hi";
    }
    @GetMapping("/path1/hello")
    public String sayhello(){
        System.out.println("hi");
        return "hi";
    }

}
