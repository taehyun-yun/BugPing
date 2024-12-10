package com.example.FinalProject.repository.employment;


import com.example.FinalProject.dto.ScheduleChangeDTO;
import com.example.FinalProject.dto.ScheduleDTO;
import com.example.FinalProject.entity.employment.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByContract_Work_User_UserId(String userId);
}


