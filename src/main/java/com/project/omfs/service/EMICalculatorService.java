package com.project.omfs.service;

import org.springframework.stereotype.Service;

@Service
public class EMICalculatorService {

    public double calculateEMI(double principal, double annualInterestRate, int months) {
        double monthlyRate = annualInterestRate / 12 / 100;
        return (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) /
                (Math.pow(1 + monthlyRate, months) - 1);
    }

    public double calculateTotalPayment(double emi, int months) {
        return emi * months;
    }

    public double calculateTotalInterest(double totalPayment, double principal) {
        return totalPayment - principal;
    }
}
