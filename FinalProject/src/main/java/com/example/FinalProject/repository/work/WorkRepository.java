package com.example.FinalProject.repository.work;

import com.example.FinalProject.entity.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * WorkRepository는 Work 엔티티에 대한 CRUD 작업을 지원합니다.
 */
public interface WorkRepository extends JpaRepository<Work, Integer> {
    List<Work> findAllByCompanyId(Long companyId);
}
