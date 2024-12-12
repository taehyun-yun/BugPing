package com.example.FinalProject.repository.work;

import com.example.FinalProject.entity.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * WorkRepository는 Work 엔티티에 대한 CRUD 작업을 지원합니다.
 */
public interface WorkRepository extends JpaRepository<Work, Integer> {

    // 회사 ID로 모든 Contract 조회
    List<Work> findByCompany_CompanyId(Integer companyId);
}
