package com.example.FinalProject.repository.user;

import com.example.FinalProject.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByUserId(String userid);
}