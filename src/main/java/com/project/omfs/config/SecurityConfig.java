package com.project.omfs.config;

import com.project.omfs.security.AdminUserDetailsService;
import com.project.omfs.security.CustomerUserDetailsService;
import com.project.omfs.security.LenderUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AdminUserDetailsService adminService;

    @Autowired
    private LenderUserDetailsService lenderService;

    @Autowired
    private CustomerUserDetailsService customerService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider adminAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider lenderAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(lenderService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider customerAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Admin
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/register").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                );

        return http.build();
    }

    // Lender
    @Bean
    @Order(2)
    public SecurityFilterChain lenderSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login", "/lender/**", "/register") // 👈 include all related URLs
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll() // 👈 allow POST login
                        .anyRequest().hasRole("LENDER")
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // 👈 matches form th:action="@{/login}"
                        .defaultSuccessUrl("/lender/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }


    // Customer
    @Bean
    @Order(3)
    public SecurityFilterChain customerSecurity(HttpSecurity http) throws Exception {
        http.securityMatcher("/customer/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/customer/register",
                                "/customer/save",      // <-- allow POST register too
                                "/customer/login",
                                "/customer/logout"
                        ).permitAll()
                        .anyRequest().hasRole("CUSTOMER")
                )
                .formLogin(form -> form
                        .loginPage("/customer/login")
                        .loginProcessingUrl("/customer/login")
                        .defaultSuccessUrl("/customer/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/customer/logout")
                        .logoutSuccessUrl("/customer/login?logout")
                );

        return http.build();
    }

    // Register all authentication providers globally
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(adminAuthProvider())
                .authenticationProvider(lenderAuthProvider())
                .authenticationProvider(customerAuthProvider())
                .build();
    }
}
