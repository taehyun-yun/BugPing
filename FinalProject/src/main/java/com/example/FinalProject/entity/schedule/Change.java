//package com.example.FinalProject.entity.schedule;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//@Entity
//@Getter
//@Setter
//@ToString
//@AllArgsConstructor
//@NoArgsConstructor
//public class Change {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer changeId;
//    @JoinColumn(name = "schedule_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Schedule schedule;
//    private LocalDate changeDate;
//    private LocalTime changeStartTime;
//    private LocalTime changeEndTime;
//    private String inOut;
//}