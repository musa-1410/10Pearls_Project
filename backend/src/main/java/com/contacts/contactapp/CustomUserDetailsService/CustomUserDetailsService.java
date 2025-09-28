package com.contacts.contactapp.CustomUserDetailsService;

import com.contacts.contactapp.Repository.userRepository;
import com.contacts.contactapp.contactManagementBackend.model.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private userRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user by email: {}", email);

        user u = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        logger.info("User found: {}", u.getEmail());

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(u.getRole()))
        );
    }
}
