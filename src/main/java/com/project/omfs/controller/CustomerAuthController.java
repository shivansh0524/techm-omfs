package com.project.omfs.controller;

import com.project.omfs.entity.Customer;
import com.project.omfs.entity.Lender;
import com.project.omfs.repository.CustomerRepository;
import com.project.omfs.repository.LenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/customer")
public class CustomerAuthController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private LenderRepository lenderRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("lenders", lenderRepo.findAll());
        return "customer-register";
    }

    @PostMapping("/save")
    public String registerCustomer(@ModelAttribute Customer customer, Model model) {

        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            model.addAttribute("error", "Name is required.");
        } else if (customer.getEmail() == null || !customer.getEmail().contains("@") || !customer.getEmail().contains(".com")) {
            model.addAttribute("error", "Invalid email format.");
        } else if (customer.getPhone() == null || !customer.getPhone().matches("\\d{10}")) {
            model.addAttribute("error", "Phone number must be exactly 10 digits.");
        } else if (customer.getPassword() == null || customer.getPassword().length() < 6 || !customer.getPassword().matches(".*[A-Z].*")) {
            model.addAttribute("error", "Password must be at least 6 characters and contain one uppercase letter.");
        } else if (customerRepo.findByEmail(customer.getEmail()) != null) {
            model.addAttribute("error", "Email already exists.");
        }

        if (model.containsAttribute("error")) {
            model.addAttribute("lenders", lenderRepo.findAll()); // repopulate dropdown
            return "customer-register";
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole("ROLE_CUSTOMER");

        // ✅ Set lender
        Lender selectedLender = lenderRepo.findById(customer.getLenderId()).orElse(null);
        customer.setLender(selectedLender);
        customer.setDate(LocalDate.now());

        customerRepo.save(customer);
        return "redirect:/customer/login";
    }



    @GetMapping("/login")
    public String customerLogin() {
        return "customer-login";
    }
}
