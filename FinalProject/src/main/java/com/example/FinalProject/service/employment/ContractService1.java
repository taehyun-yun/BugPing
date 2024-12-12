package com.example.FinalProject.service.employment;

import com.example.FinalProject.entity.employment.Contract;
import com.example.FinalProject.repository.employment.ContractRepository1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService1 {

    @Autowired
    private ContractRepository1 contractRepository1;

    public List<Contract> getContractsByCompanyId(String companyId) {
        return contractRepository1.findByCompanyId(companyId);
    }
}

