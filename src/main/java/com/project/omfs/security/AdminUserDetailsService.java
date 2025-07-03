package com.project.omfs.security;

import com.project.omfs.entity.Admin;
import com.project.omfs.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByEmail(email);
        if (admin == null) throw new UsernameNotFoundException("Admin not found");
        return new AdminUserDetails(admin);
    }
}
