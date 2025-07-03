//package com.project.omfs.controller;
//
//import com.project.omfs.entity.Customer;
//import com.project.omfs.entity.Lender;
//import com.project.omfs.repository.CustomerRepository;
//import com.project.omfs.repository.LenderRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.security.Principal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/lender")
//public class LenderController {
//
//    @Autowired
//    private LenderRepository lenderRepo;
//
//    @Autowired
//    private CustomerRepository customerRepo;
//
//    // ✅ Updated lender dashboard with filters and summary
//    @GetMapping("/dashboard")
//    public String dashboardPage(Model model,
//                                Principal principal,
//                                @RequestParam(required = false) String status,
//                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
//                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
//
//        Lender lender = lenderRepo.findByEmail(principal.getName());
//
//        List<Customer> customers = customerRepo.findAll().stream()
//                .filter(c -> c.getLender() != null && c.getLender().getId().equals(lender.getId()))
//                .filter(c -> status == null || status.isEmpty() || c.getStatus().equalsIgnoreCase(status))
//                .filter(c -> from == null || (c.getDate() != null && !c.getDate().isBefore(from)))
//                .filter(c -> to == null || (c.getDate() != null && !c.getDate().isAfter(to)))
//                .toList();
//
//        long total = customers.size();
//        long approved = customers.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count();
//        long rejected = customers.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count();
//
//        model.addAttribute("lender", lender);
//        model.addAttribute("customers", customers);
//        model.addAttribute("total", total);
//        model.addAttribute("approved", approved);
//        model.addAttribute("rejected", rejected);
//        model.addAttribute("selectedStatus", status);
//        model.addAttribute("from", from);
//        model.addAttribute("to", to);
//
//        return "lender-dashboard";
//    }
//
//    // ✅ View all loan applications
//    @GetMapping("/loans")
//    public String viewAllLoans(Model model) {
//        model.addAttribute("applications", customerRepo.findAll());
//        return "loan-approval-list";
//    }
//
//    @PostMapping("/approve/{id}")
//    public String approveCustomer(@PathVariable Long id) {
//        Optional<Customer> optionalCustomer = customerRepo.findById(id);
//        if (optionalCustomer.isPresent()) {
//            Customer customer = optionalCustomer.get();
//            customer.setStatus("APPROVED");
//            customerRepo.save(customer);
//        }
//        return "redirect:/lender/dashboard";
//    }
//
//    @PostMapping("/reject/{id}")
//    public String rejectCustomer(@PathVariable Long id) {
//        Optional<Customer> optionalCustomer = customerRepo.findById(id);
//        if (optionalCustomer.isPresent()) {
//            Customer customer = optionalCustomer.get();
//            customer.setStatus("REJECTED");
//            customerRepo.save(customer);
//        }
//        return "redirect:/lender/dashboard";
//    }
//
//}
package com.project.omfs.controller;

import com.project.omfs.entity.Customer;
import com.project.omfs.entity.Lender;
import com.project.omfs.entity.RepaymentSchedule;
import com.project.omfs.repository.CustomerRepository;
import com.project.omfs.repository.LenderRepository;
import com.project.omfs.repository.RepaymentScheduleRepository;
import com.project.omfs.service.RepaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/lender")
public class LenderController {

    @Autowired
    private LenderRepository lenderRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private RepaymentScheduleRepository scheduleRepo;

    @Autowired
    private RepaymentService repaymentService;

    // ✅ Lender Dashboard with Filter and Summary
    @GetMapping("/dashboard")
    public String dashboardPage(Model model,
                                Principal principal,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Lender lender = lenderRepo.findByEmail(principal.getName());

        List<Customer> customers = customerRepo.findAll().stream()
                .filter(c -> c.getLender() != null && c.getLender().getId().equals(lender.getId()))
                .filter(c -> status == null || status.isEmpty() || c.getStatus().equalsIgnoreCase(status))
                .filter(c -> from == null || (c.getDate() != null && !c.getDate().isBefore(from)))
                .filter(c -> to == null || (c.getDate() != null && !c.getDate().isAfter(to)))
                .toList();

        long total = customers.size();
        long approved = customers.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count();
        long rejected = customers.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count();

        model.addAttribute("lender", lender);
        model.addAttribute("customers", customers);
        model.addAttribute("total", total);
        model.addAttribute("approved", approved);
        model.addAttribute("rejected", rejected);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        return "lender-dashboard";
    }

    // ✅ Approve Customer
    @PostMapping("/approve/{id}")
    public String approveCustomer(@PathVariable Long id) {
        Optional<Customer> optionalCustomer = customerRepo.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setStatus("APPROVED");
            customerRepo.save(customer);

            List<RepaymentSchedule> schedule = repaymentService.generateSchedule(customer, 10.0);
            scheduleRepo.saveAll(schedule);
        }
        return "redirect:/lender/dashboard";
    }

    // ✅ Reject Customer
    @PostMapping("/reject/{id}")
    public String rejectCustomer(@PathVariable Long id) {
        Optional<Customer> optionalCustomer = customerRepo.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setStatus("REJECTED");
            customerRepo.save(customer);
        }
        return "redirect:/lender/dashboard";
    }

    // ✅ Lender Loan Schedule Report
    @GetMapping("/schedule-report")
    public String loanScheduleReport(Model model, Principal principal) {
        Lender lender = lenderRepo.findByEmail(principal.getName());
        List<Customer> customers = customerRepo.findByLender(lender);
        model.addAttribute("customers", customers);
        return "lender-loan-schedule";
    }

    // ✅ View Schedule for Customer
    @GetMapping("/schedule/{id}")
    public String viewSchedule(@PathVariable Long id, Model model) {
        Optional<Customer> optional = customerRepo.findById(id);
        if (optional.isPresent()) {
            Customer customer = optional.get();
            List<RepaymentSchedule> schedule = scheduleRepo.findByCustomer(customer);
            model.addAttribute("customer", customer);
            model.addAttribute("schedule", schedule);
//            return "repayment-schedule";
            return "lender-loan-schedule";
        }
        return "redirect:/lender/schedule-report";
    }
}