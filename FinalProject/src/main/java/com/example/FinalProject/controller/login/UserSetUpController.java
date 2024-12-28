package com.example.FinalProject.controller.login;

import com.example.FinalProject.service.employment.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class UserSetUpController {
    final private ContractService contractService;

    UserSetUpController(ContractService contractService){
        this.contractService = contractService;
    }
    //헤더에 companyList 넣기
    @GetMapping("/getHeaderCompanyList")
    public ResponseEntity<List<Map<String,Object>>> sendMyCompany(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return new ResponseEntity<>(contractService.getWorkList(userId), HttpStatus.OK);
    }
}
