package com.example.FinalProject.repository.employment;

import com.example.FinalProject.entity.employment.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Integer> {
    @Query("SELECT DISTINCT c FROM Contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "JOIN FETCH cp.user uu " +
            "WHERE c.id = :contractId")
    Contract findContractWithWorkAndUser(@Param("contractId") Integer contractId);

    @Query("SELECT DISTINCT c FROM Contract c " +
            "JOIN FETCH c.work w " +
            "JOIN FETCH w.user u " +
            "JOIN FETCH w.company cp " +
            "JOIN FETCH cp.user uu ")
    List<Contract> findAllContractsWithWorkAndUser();

}
