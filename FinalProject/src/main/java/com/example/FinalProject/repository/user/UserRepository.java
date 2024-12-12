package com.example.FinalProject.repository.user;

import com.example.FinalProject.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByUserId(String userid);

    @Query("SELECT u.id FROM User u WHERE u.role = 'Employee' ORDER BY u.id ASC")
    Page<Integer> findAllEmployeeIds(Pageable pageable);

    @Query("SELECT u.name FROM User u WHERE u.userId = :userId")
    String findNameByUserId(String userId);
}