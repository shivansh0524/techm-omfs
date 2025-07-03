package com.project.omfs.repository;

import com.project.omfs.entity.Customer;
import com.project.omfs.entity.Lender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByEmail(String email);
    List<Customer> findAllByEmail(String email);
    List<Customer> findByStatus(String status);

    List<Customer> findByLender(Lender lender);

    List<Customer> findByStatusAndLender(String status, Lender lender);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = :status")
    long countByStatus(String status);

    @Query("SELECT COUNT(c) FROM Customer c")
    long countAll();

    @Query("SELECT c FROM Customer c WHERE "
            + "(:status IS NULL OR c.status = :status) AND "
            + "(:lenderId IS NULL OR c.lender.id = :lenderId) AND "
            + "(:from IS NULL OR c.date >= :from) AND "
            + "(:to IS NULL OR c.date <= :to)")
    List<Customer> filterLoans(String status, Long lenderId, LocalDate from, LocalDate to);

}
