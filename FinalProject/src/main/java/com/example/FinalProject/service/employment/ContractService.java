package com.example.FinalProject.service.employment;


import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.work.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService {

    @Autowired
    private WorkRepository WorkRepository;

}

