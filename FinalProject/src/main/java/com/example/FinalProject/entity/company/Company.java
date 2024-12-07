package com.example.FinalProject.entity.company;

import com.example.FinalProject.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Company {
    @Id
    private String companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private String cname;
    private String ctel;
    private String address;
    private String cnum;
}
