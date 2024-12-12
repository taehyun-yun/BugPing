package com.example.FinalProject.repository.user;

import com.example.FinalProject.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByUserId(String userid);
    @Query("SELECT u.userId from User u where u.email = :email")
    Optional<List<String>>findByEmail(String email);
}