// FileRepository.java (Complete)
package com.example.FinalProject.repository.notice;

import com.example.FinalProject.entity.file.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
}
