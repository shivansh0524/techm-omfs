package com.project.omfs.service;

import com.project.omfs.entity.Customer;
import com.project.omfs.entity.RepaymentSchedule;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class RepaymentService {
    public List<RepaymentSchedule> generateSchedule(Customer customer, double annualRate) {
        List<RepaymentSchedule> schedule = new ArrayList<>();
        int months = customer.getLoanTermMonths();
        double loanAmount = customer.getLoanAmount();
        double monthlyRate = annualRate / 12 / 100;

        double emi = (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, months)) /
                (Math.pow(1 + monthlyRate, months) - 1);

        double balance = loanAmount;

        for (int i = 1; i <= months; i++) {
            double interest = balance * monthlyRate;
            double principal = emi - interest;
            balance -= principal;

            RepaymentSchedule r = new RepaymentSchedule();
            r.setInstallmentNumber(i);
            r.setDueDate(LocalDate.now().plusMonths(i));
            r.setPrincipal(Math.round(principal));
            r.setInterest(Math.round(interest));
            r.setEmi(Math.round(emi));
            r.setRemainingPrincipal(Math.max(0, Math.round(balance)));
            r.setCustomer(customer);
            schedule.add(r);
        }

        return schedule;
    }
}
