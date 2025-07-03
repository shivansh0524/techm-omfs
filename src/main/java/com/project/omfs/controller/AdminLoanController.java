package com.project.omfs.controller;

import com.project.omfs.entity.Customer;
import com.project.omfs.repository.CustomerRepository;
import com.project.omfs.repository.LenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminLoanController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private LenderRepository lenderRepo;

    @GetMapping("/reports")
    public String showLoanReport(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long lenderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        List<Customer> filtered = customerRepo.filterLoans(status, lenderId, from, to);
        long total = customerRepo.countAll();
        long approved = customerRepo.countByStatus("APPROVED");
        long rejected = customerRepo.countByStatus("REJECTED");

        model.addAttribute("customers", filtered);
        model.addAttribute("lenders", lenderRepo.findAll());
        model.addAttribute("total", total);
        model.addAttribute("approved", approved);
        model.addAttribute("rejected", rejected);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedLenderId", lenderId);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        return "admin-loan-reports";
    }
}
