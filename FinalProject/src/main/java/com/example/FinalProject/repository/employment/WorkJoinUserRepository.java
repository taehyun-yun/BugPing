package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.user.User;
import com.example.FinalProject.entity.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WorkJoinUserRepository extends JpaRepository<Work, Integer> {


    // 특정 회사의 모든 근무 직원 조회
    @Query("SELECT w FROM Work w JOIN FETCH w.user u WHERE w.company.companyId = :companyId")
    List<Work> findAllWorksByCompanyId(@Param("companyId") Integer companyId);

    // 특정 회사의 직원 검색 (이름/사번/전화번호 포함)
    @Query("SELECT w FROM Work w JOIN FETCH w.user u WHERE w.company.companyId = :companyId " +
            "AND (" +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "LOWER(u.tel) LIKE LOWER(CONCAT('%', :searchQuery, '%'))" +
            ")")
    List<Work> searchAllWorksByCompanyId(@Param("companyId") Integer companyId,
                                         @Param("searchQuery") String searchQuery);
}
