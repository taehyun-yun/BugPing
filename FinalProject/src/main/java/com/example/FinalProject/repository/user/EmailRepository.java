package com.example.FinalProject.repository.user;

import com.example.FinalProject.entity.user.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email,Integer> {
    Optional<Email> findByUser_userId (String userId);
}