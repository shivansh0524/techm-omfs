package com.project.omfs.security;

import com.project.omfs.entity.Customer;
import com.project.omfs.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("customerDetailsService")
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepo.findByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException("Customer not found");
        }

        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .authorities(customer.getRole())
                .build();
    }
}
