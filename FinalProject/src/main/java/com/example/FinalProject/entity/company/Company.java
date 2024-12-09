package com.example.FinalProject.entity.company;

import com.example.FinalProject.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
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
