package com.project.omfs.repository;

import com.project.omfs.entity.Customer;
import com.project.omfs.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    List<RepaymentSchedule> findByCustomer(Customer customer);
}
