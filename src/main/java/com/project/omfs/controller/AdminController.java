package com.project.omfs.controller;

import com.project.omfs.entity.Admin;
import com.project.omfs.entity.Customer;
import com.project.omfs.repository.AdminRepository;
import com.project.omfs.repository.CustomerRepository;
import com.project.omfs.repository.LenderRepository;
import com.project.omfs.service.EMICalculatorService;
import com.project.omfs.util.PDFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.project.omfs.entity.RepaymentSchedule;
import com.project.omfs.repository.RepaymentScheduleRepository;
import com.project.omfs.service.RepaymentService;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private RepaymentService repaymentService;
    @Autowired
    private RepaymentScheduleRepository repaymentRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private EMICalculatorService emiService;
    @Autowired
    private LenderRepository lenderRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin-register";
    }

    @PostMapping("/register")
    public String registerAdmin(@ModelAttribute Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepo.save(admin);
        return "redirect:/admin/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "admin-login";
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Customer> all = customerRepo.findAll();
        model.addAttribute("customers", all);
        model.addAttribute("total", all.size());
        model.addAttribute("approved", all.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("rejected", all.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count());
        return "admin-dashboard";
    }
    @GetMapping("/loans")
    public String redirectToLoanReport() {
        return "redirect:/admin/dashboard";
    }



    @GetMapping("/loan-details/{id}")
    public String viewLoanDetails(@PathVariable Long id, Model model) {
        Customer customer = customerRepo.findById(id).orElseThrow();

        double emi = emiService.calculateEMI(customer.getLoanAmount(), 10.0, customer.getLoanTermMonths());
        double totalPayment = emiService.calculateTotalPayment(emi, customer.getLoanTermMonths());
        double totalInterest = emiService.calculateTotalInterest(totalPayment, customer.getLoanAmount());

        model.addAttribute("customer", customer);
        model.addAttribute("emi", Math.round(emi));
        model.addAttribute("totalInterest", Math.round(totalInterest));
        model.addAttribute("totalPayment", Math.round(totalPayment));

        return "admin-loan-detail";
    }


    @GetMapping("/filter")
    public String filterLoans(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String lenderEmail,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Model model
    ) {
        List<Customer> filtered = customerRepo.findAll();
        if (status != null && !status.isEmpty()) {
            filtered = filtered.stream()
                    .filter(c -> c.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        if (lenderEmail != null && !lenderEmail.isEmpty()) {
            filtered = filtered.stream()
                    .filter(c -> c.getLender() != null && c.getLender().getEmail().equalsIgnoreCase(lenderEmail))
                    .collect(Collectors.toList());
        }
        if (from != null && to != null && !from.isEmpty() && !to.isEmpty()) {
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);
            filtered = filtered.stream()
                    .filter(c -> !c.getDate().isBefore(fromDate) && !c.getDate().isAfter(toDate))
                    .collect(Collectors.toList());
        }
        model.addAttribute("customers", filtered);
        model.addAttribute("lenders", lenderRepo.findAll());
        model.addAttribute("total", filtered.size());
        model.addAttribute("approved", filtered.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("rejected", filtered.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count());

        return "admin-dashboard";
    }


    @PostMapping("/approve/{id}")
    public String approveFromAdmin(@PathVariable Long id) {
//        customerRepo.findById(id).ifPresent(c -> {
//            c.setStatus("APPROVED");
//            customerRepo.save(c);
//        });
//        return "redirect:/admin/dashboard";
        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer != null && !"APPROVED".equalsIgnoreCase(customer.getStatus())) {
            customer.setStatus("APPROVED");
            customerRepo.save(customer);
            List<RepaymentSchedule> schedule = repaymentService.generateSchedule(customer, 10.0); // 10% interest
            repaymentRepo.saveAll(schedule);
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/reject/{id}")
    public String rejectFromAdmin(@PathVariable Long id) {
        customerRepo.findById(id).ifPresent(c -> {
            c.setStatus("REJECTED");
            customerRepo.save(c);
        });
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/loan-reports")
    public String loanReport(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long lenderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            Model model) {

        List<Customer> filtered = customerRepo.findAll().stream()
                .filter(c -> status == null || status.isEmpty() || c.getStatus().equalsIgnoreCase(status))
                .filter(c -> lenderId == null || (c.getLender() != null && c.getLender().getId().equals(lenderId)))
                .filter(c -> from == null || (c.getDate() != null && !c.getDate().isBefore(from)))
                .collect(Collectors.toList());

        model.addAttribute("customers", filtered);
        model.addAttribute("lenders", lenderRepo.findAll());
        model.addAttribute("total", filtered.size());
        model.addAttribute("approved", filtered.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("rejected", filtered.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count());

        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedLenderId", lenderId);
        model.addAttribute("from", from);

        return "admin-loan-reports";
    }

    @GetMapping("/loan-schedule/{id}")
    public String viewSchedule(@PathVariable Long id, Model model) {
        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer != null) {
            List<RepaymentSchedule> schedule = repaymentRepo.findByCustomer(customer);
            model.addAttribute("customer", customer);
            model.addAttribute("schedule", schedule);
        }
        return "admin-loan-schedule";
    }

    @GetMapping("/schedule/{id}")
    public String showRepaymentSchedule(@PathVariable Long id, Model model) {
        Customer customer = customerRepo.findById(id).orElseThrow();
        List<RepaymentSchedule> schedule = repaymentService.generateSchedule(customer, 10); // assume 10% rate
        model.addAttribute("schedule", schedule);
        model.addAttribute("customer", customer);
        return "admin-loan-schedule";
    }

    @GetMapping("/download-pdf/{id}")
    public ResponseEntity<byte[]> downloadLoanPdf(@PathVariable Long id) {
        Customer customer = customerRepo.findById(id).orElseThrow();
        ByteArrayInputStream pdf = PDFGenerator.generateLoanReport(customer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=loan_report_" + customer.getId() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.readAllBytes());
    }



}
