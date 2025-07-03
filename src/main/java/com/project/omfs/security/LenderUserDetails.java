package com.project.omfs.security;

import com.project.omfs.entity.Lender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class LenderUserDetails implements UserDetails {

    private final Lender lender;

    public LenderUserDetails(Lender lender) {
        this.lender = lender;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(lender.getRole()));
    }

    @Override
    public String getPassword() {
        return lender.getPassword();
    }

    @Override
    public String getUsername() {
        return lender.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
