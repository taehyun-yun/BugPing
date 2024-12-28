package com.example.FinalProject.repository.notice;

import com.example.FinalProject.entity.notice.Notice;
import com.example.FinalProject.entity.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findByType(String type);
    List<Notice> findByWork_Company_CompanyId(Integer companyId);
    List<Notice> findByTypeAndWork_Company_CompanyId(String type, Integer companyId);
}
