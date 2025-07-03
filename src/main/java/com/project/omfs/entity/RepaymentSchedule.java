package com.project.omfs.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int installmentNumber;
    private LocalDate dueDate;
    private double principal;
    private double interest;
    private double emi;
    private double remainingPrincipal;

    @ManyToOne
    private Customer customer;
}
