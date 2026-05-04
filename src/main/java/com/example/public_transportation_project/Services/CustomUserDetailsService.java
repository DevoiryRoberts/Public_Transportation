package com.example.public_transportation_project.Services;

import com.example.public_transportation_project.Models.Driver;
import com.example.public_transportation_project.Repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    System.out.println("Attempting to login with email: " + email);
    Driver driver = driverRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));

    String cleanRole = driver.getRole().replace("ROLE_", "");

    return User.builder()
        .username(driver.getEmail())
        .password(driver.getPassword()) 
        .roles(cleanRole) 
        .build();
}

}