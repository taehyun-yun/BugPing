package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    // 특정 계약 ID에 연관된 스케줄을 가져옵니다
    List<Schedule> findByContractId(Integer contractId);
}
