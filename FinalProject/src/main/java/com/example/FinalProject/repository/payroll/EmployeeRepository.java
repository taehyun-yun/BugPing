package com.example.FinalProject.repository.payroll;

import com.example.FinalProject.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<User, Long> {

}
