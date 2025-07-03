package com.project.omfs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.processing.Pattern;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Invalid email")
    private String email;
    @Column(nullable = false)
    private String password;

    private String phone;

    private Double income;

    private Double loanAmount;

    private String role = "ROLE_CUSTOMER";

    private String status = "PENDING";

    private LocalDate date; // make sure this field exists
    @Transient
    private Long lenderId;
    private int loanTermMonths = 12;

    @ManyToOne
    @JoinColumn(name = "lender_id")
    private Lender lender;  // make sure this relationship exists
}
