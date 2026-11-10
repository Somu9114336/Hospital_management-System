package com.HMS.hospital_management_system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JWT FILTER EXECUTED");
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            System.out.println("TOKEN EXTRACTED");

            if (jwtService.validateToken(token)) {
                System.out.println("TOKEN IS VALID");
                String email = jwtService.extractEmail(token);
                System.out.println("USER EMAIL: " + email);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("AUTHENTICATION SET: " +
                        SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

            }
            else {
                System.out.println("TOKEN IS INVALID");
            }

        }
        System.out.println("BEFORE FILTER CHAIN: " +
                SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(req,res);

        System.out.println("AFTER FILTER CHAIN: " +
                SecurityContextHolder.getContext().getAuthentication());


    }

}
