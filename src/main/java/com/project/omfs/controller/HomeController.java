package com.project.omfs.controller;

import com.project.omfs.entity.Lender;
import com.project.omfs.repository.LenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private LenderRepository lenderRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Public home page showing all lenders (optional)
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("lenders", lenderRepo.findAll());
        return "home";
    }

    // Show lender registration form
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("lender", new Lender());
        return "register";
    }

    // Submit lender registration
    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute Lender lender) {
        lender.setPassword(passwordEncoder.encode(lender.getPassword()));
        lender.setRole("ROLE_LENDER");
        lenderRepo.save(lender);
        return "redirect:/login";
    }

    // Show lender login form
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
