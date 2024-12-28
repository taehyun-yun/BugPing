package com.example.FinalProject.repository.work;

import com.example.FinalProject.entity.company.Company;
import com.example.FinalProject.entity.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * WorkRepository는 Work 엔티티에 대한 CRUD 작업을 지원합니다.
 */
public interface WorkRepository extends JpaRepository<Work, Integer> {

    // 회사 ID로 모든 Contract 조회
    List<Work> findByCompany_CompanyId(Integer companyId);

    // userId로 company 조회할때 사용
    Work findByUser_UserId(String userId);

//--------------------------Joonho----------------------------
    //재직중인지 아닌지
    boolean existsByUser_UserIdAndCompany_CompanyIdAndResignDateIsNull (String userId,Integer companyId);
    //재직했던 근무처 목록. 최신순
    Optional<List<Work>> findByUser_userIdOrderByHireDateDesc(String userId);
    //역할에 따른 회사 목록.
    Optional<List<Work>> findByUser_userIdAndUser_Role(String userId, String role);
    //여러 회사 목록에 따라 불러오기
    @Query("SELECT w FROM Work w WHERE w.company in (:companies) ")
    Optional<List<Work>> findMyCompaniesWorkers(List<Company> companies);

//--------------------------JJang----------------------------

  Optional<Work> findTopByUser_userIdAndCompany_CompanyIdOrderByHireDateDesc(String userId, Integer companyId);


}
