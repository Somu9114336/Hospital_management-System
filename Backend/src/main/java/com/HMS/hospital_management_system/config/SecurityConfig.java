package com.HMS.hospital_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private  final JwtFilter jwtFilter;
    private static final RequestMatcher PUBLIC_AUTH_ENDPOINTS = request -> {
        String requestPath = request.getRequestURI();
        return HttpMethod.POST.matches(request.getMethod())
                && (requestPath.endsWith("/user/register") || requestPath.endsWith("/user/login"));
    };

    SecurityConfig(JwtFilter jwtFilter){
        this.jwtFilter=jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers(PUBLIC_AUTH_ENDPOINTS).permitAll()

                        // Doctor read access
                        .requestMatchers(HttpMethod.GET,
                                "/doctor/getall",
                                "/doctor/get/*",
                                "/doctor/specialization/**",
                                "/doctor/getbyname/**"
                        ).hasAnyRole("ADMIN", "DOCTOR", "PATIENT")

                        // Doctor management - ADMIN only
                        .requestMatchers("/doctor/**").hasRole("ADMIN")

                        // Patient management - ADMIN only
                        .requestMatchers("/patient/**").hasRole("ADMIN")

                        // Patient appointment access
                        .requestMatchers(HttpMethod.POST, "/appointments/book")
                        .hasRole("PATIENT")

                        .requestMatchers(HttpMethod.GET,
                                "/appointments/getallmyappointments",
                                "/appointments/get/patient/**",
                                "/appointments/get/*"
                        ).hasAnyRole("ADMIN", "PATIENT")

                        .requestMatchers(HttpMethod.PUT, "/appointments/reshedule/*")
                        .hasAnyRole("ADMIN", "PATIENT")

                        .requestMatchers(HttpMethod.PATCH, "/appointments/cancle/*")
                        .hasAnyRole("ADMIN", "PATIENT")

                        // Doctor appointment viewing - read-only
                        .requestMatchers(HttpMethod.GET,
                                "/appointments/get/doctor/**"
                        ).hasAnyRole("ADMIN", "DOCTOR")

                        // All remaining appointment operations - ADMIN only
                        .requestMatchers("/appointments/**").hasRole("ADMIN")

                        // Other authenticated endpoints
                        .anyRequest().authenticated()
                );

        httpSecurity.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
