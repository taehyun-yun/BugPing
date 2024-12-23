package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Schedule;
import com.example.FinalProject.entity.employment.WorkChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface WorkChangeRepository extends JpaRepository<WorkChange, Integer> {
    // 특정 스케줄과 IN 상태로 가장 최근 WorkChange 조회
    Optional<WorkChange> findFirstByScheduleAndInOutOrderByWorkChangeIdDesc(Schedule schedule, String inOut);

    // 특정 스케줄 ID로 모든 WorkChange 조회
    List<WorkChange> findBySchedule(Schedule schedule);

    // 특정 스케줄과 IN 상태의 모든 WorkChange 조회
    List<WorkChange> findByScheduleAndInOut(Schedule schedule, String inOut);
}



