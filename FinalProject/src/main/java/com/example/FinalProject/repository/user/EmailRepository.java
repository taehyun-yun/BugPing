package com.example.FinalProject.repository.user;

import com.example.FinalProject.entity.user.EmailCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailCheck,Integer> {
    Optional<EmailCheck> findByEmail (String email);
}