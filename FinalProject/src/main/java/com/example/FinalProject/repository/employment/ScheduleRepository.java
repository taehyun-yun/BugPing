package com.example.FinalProject.repository.employment;


import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    // 특정 Contract ID로 스케줄을 조회하는 메서드
    List<Schedule> findByContractContractId(Integer contractId);
}
