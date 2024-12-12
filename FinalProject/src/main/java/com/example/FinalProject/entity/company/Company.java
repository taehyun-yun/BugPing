package com.example.FinalProject.entity.company;

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

    private String cname;
    private String ctel;
    private String address;
    private String cnum;
}
