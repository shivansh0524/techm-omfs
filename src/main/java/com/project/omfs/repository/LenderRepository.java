package com.project.omfs.repository;

import com.project.omfs.entity.Lender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LenderRepository extends JpaRepository<Lender, Long> {
    Lender findByEmail(String email);
}
