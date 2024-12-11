package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // 모든 데이터 Page
    Page<Schedule> findAll(Pageable pageable);

    // userId의 전체 일정 반환
    List<Schedule> findByContract_Work_User_UserId(String userId);

    // UserId의 페이징 처리
    Page<Schedule> findByOfficialStartBetween(LocalDate start, LocalDate end, Pageable pageable);

}


