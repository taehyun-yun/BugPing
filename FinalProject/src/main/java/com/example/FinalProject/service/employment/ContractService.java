package com.example.FinalProject.service.employment;


import com.example.FinalProject.entity.work.Work;
import com.example.FinalProject.repository.work.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContractService {

    @Autowired
    final private WorkRepository workRepository;

    ContractService (WorkRepository workRepository){
        this.workRepository = workRepository;
    }

    public List<Map<String,Object>>getWorkList(String userId){
        Optional<List<Work>> workList = workRepository.findByUser_userIdOrderByHireDateDesc(userId);
        List<Map<String,Object>> response = new LinkedList<>();
        if(workList.isPresent()){
            List<Work> list = workList.get();
            for(Work elem : list){
                Map<String,Object> map = new HashMap<>();
                map.put("companyId",elem.getCompany().getCompanyId());
                map.put("cname",elem.getCompany().getCname());
                response.add(map);
            }
        }
        return response;
    }
}

