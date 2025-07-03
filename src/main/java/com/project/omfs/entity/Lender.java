package com.project.omfs.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String role="ROLE_USER";

    @OneToMany(mappedBy = "lender", cascade = CascadeType.ALL)
    private List<Customer> customers;

}
