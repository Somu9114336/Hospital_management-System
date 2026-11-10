package com.HMS.hospital_management_system.service;

import com.HMS.hospital_management_system.entity.User;
import com.HMS.hospital_management_system.repositary.UserRepositary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepositary userRepositary;

    CustomUserDetailsService(UserRepositary userRepositary){
        this.userRepositary=userRepositary;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        User user=userRepositary.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
