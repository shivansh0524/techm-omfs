package com.project.omfs.controller;

import com.project.omfs.entity.Customer;
import com.project.omfs.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepo;

    @GetMapping("/apply")
    public String showApplicationForm(Model model, Principal principal) {
        String email = principal.getName();
        Customer existing = customerRepo.findByEmail(email);

        if (existing == null) {
            model.addAttribute("error", "You must be logged in to apply.");
            return "redirect:/login";
        }

        // Only show empty fields for loan info
        Customer loanForm = new Customer();
        loanForm.setName(existing.getName()); // optional if needed
        model.addAttribute("customer", loanForm);

        return "customer-apply";
    }

    @PostMapping("/apply")
    public String submitApplication(@ModelAttribute Customer formCustomer, Model model, Principal principal) {
        String email = principal.getName();
        Customer existing = customerRepo.findByEmail(email);

        if (existing == null) {
            model.addAttribute("error", "You must be logged in to apply.");
            return "redirect:/login";
        }

        // Update only loan fields
        existing.setLoanAmount(formCustomer.getLoanAmount());
        existing.setIncome(formCustomer.getIncome());
        existing.setDate(LocalDate.now());
        existing.setStatus("PENDING");

        customerRepo.save(existing);
        model.addAttribute("message", "Application submitted successfully!");
        return "customer-apply";
    }

    @GetMapping("/view")
    public String viewMyApplications(Model model, Principal principal) {
        String email = principal.getName();
        List<Customer> myLoans = customerRepo.findAllByEmail(email);
        model.addAttribute("customers", myLoans);
        return "customer-list";
    }

    @GetMapping("/dashboard")
    public String customerDashboard() {
        return "customer-dashboard";
    }
}
