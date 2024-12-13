package com.example.FinalProject.repository.user;

import com.example.FinalProject.dto.UserFindIdDTO;
import com.example.FinalProject.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByUserId(String userid);
    @Query("SELECT new com.example.FinalProject.dto.UserFindIdDTO(u.userId, u.regDate) " +
            "FROM User u WHERE u.email = :email")
    Optional<List<UserFindIdDTO>> findUserIdAndRegDateByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.role = 'Employee' ORDER BY u.id ASC")
    Page<Integer> findAllEmployeeIds(Pageable pageable);

    @Query("SELECT u.name FROM User u WHERE u.userId = :userId")
    String findNameByUserId(String userId);

}