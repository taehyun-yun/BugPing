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
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer companyId;
    private String cname;
    private String ctel;
    private String address;
    private String detailAddress;

    @Column(unique = true)
    private String cnum;//사업자 등록번호
    @Column(unique = true)
    private String companyCode;
}
