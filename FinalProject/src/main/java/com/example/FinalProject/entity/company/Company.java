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
    @Column(name = "companyId")
    private String companyId;
    @ManyToOne(fetch = FetchType.LAZY)
<<<<<<< HEAD
//    @JoinColumn(name = "user_id")
//    private User user;
=======
    @JoinColumn(name = "userId")
    private User user;
>>>>>>> 564d4d18815f3c378fed769b10d81ebfe74b855b
    private String cname;
    private String ctel;
    private String address;
    private String cnum;
}
