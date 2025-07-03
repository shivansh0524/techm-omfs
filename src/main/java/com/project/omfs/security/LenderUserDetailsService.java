package com.project.omfs.security;

import com.project.omfs.entity.Lender;
import com.project.omfs.repository.LenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class    LenderUserDetailsService implements UserDetailsService {

    @Autowired
    private LenderRepository lenderRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Lender lender = lenderRepo.findByEmail(email);
        if (lender == null) throw new UsernameNotFoundException("User not found");
        return new LenderUserDetails(lender);
    }
}
