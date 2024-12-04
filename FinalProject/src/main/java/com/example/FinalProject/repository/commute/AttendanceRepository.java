package com.example.FinalProject.repository.commute;

import com.example.FinalProject.entity.commute.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
}
