package com.example.FinalProject.repository.user;

import com.example.FinalProject.entity.user.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email,Integer> {
}
