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
    @Column(name = "companyId")
    private String companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User userId;
    private String cname;
    private String ctel;
    private String address;
    private String cnum;
}
