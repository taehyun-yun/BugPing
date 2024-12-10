package com.example.FinalProject.controller.employment;

import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.repository.employment.WorkChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workChanges")
public class WorkChangeController {

    @Autowired
    private WorkChangeRepository workChangeRepository;

    @GetMapping
    public List<WorkChange> getAllWorkChanges() {
        return workChangeRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<WorkChange> createWorkChange(@RequestBody WorkChange workChange) {
        WorkChange savedWorkChange = workChangeRepository.save(workChange);
        return ResponseEntity.ok(savedWorkChange);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkChange> updateWorkChange(@PathVariable Integer id, @RequestBody WorkChange workChangeDetails) {
        return workChangeRepository.findById(id).map(workChange -> {
            workChange.setChangeStartTime(workChangeDetails.getChangeStartTime());
            workChange.setChangeEndTime(workChangeDetails.getChangeEndTime());
            workChange.setInOut(workChangeDetails.getInOut());
            workChange.setChangeDate(workChangeDetails.getChangeDate());
            WorkChange updatedWorkChange = workChangeRepository.save(workChange);
            return ResponseEntity.ok(updatedWorkChange);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteWorkChange(@PathVariable Integer id) {
        return workChangeRepository.findById(id).map(workChange -> {
            workChangeRepository.delete(workChange);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
