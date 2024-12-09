package com.example.FinalProject.dto;

import com.example.FinalProject.entity.employment.WorkChange;
import com.example.FinalProject.entity.employment.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleAndChangeDTO {
    private List<Schedule> schedules;
    private List<WorkChange> workChanges;
}
