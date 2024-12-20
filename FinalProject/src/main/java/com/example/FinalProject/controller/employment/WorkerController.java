package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.employment.WorkJoinUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WorkerController {

    @Autowired
    private WorkJoinUserRepository workJoinUserRepository;
    /**
     * 특정 회사의 모든 근무 직원 목록 조회
     */
    @GetMapping("/worker/{companyId}")
    public ResponseEntity<List<Work>> getAllWorksByCompanyId(@PathVariable Integer companyId) {
        List<Work> works = workJoinUserRepository.findAllWorksByCompanyId(companyId);
        return new ResponseEntity<>(works, HttpStatus.OK);
    }

    /**
     * 특정 회사에서 검색 조건에 따른 직원 목록 조회
     */
    @GetMapping("/worker/{companyId}/search")
    public ResponseEntity<List<Work>> searchAllWorksByCompanyId(@PathVariable Integer companyId,
                                                                @RequestParam(required = false) String searchQuery) {
        List<Work> works;
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            works = workJoinUserRepository.findAllWorksByCompanyId(companyId);
        } else {
            works = workJoinUserRepository.searchAllWorksByCompanyId(companyId, searchQuery);
        }
        return new ResponseEntity<>(works, HttpStatus.OK);
    }
}