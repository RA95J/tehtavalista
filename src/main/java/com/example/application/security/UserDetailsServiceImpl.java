package com.example.application.security;

import com.example.application.data.Role;
import com.example.application.data.User;
import com.example.application.data.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;

    if (userRepository.count() == 0) {
        // Luodaan normaali käyttäjä
        User user = new User();
        user.setUsername("user");
        String userPassword = "user";
        user.setHashedPassword(passwordEncoder.encode(userPassword));

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(Role.USER);
        user.setRoles(userRoles);
        this.userRepository.save(user);

        // Luodaan admin käyttäjä
        User admin = new User();
        admin.setUsername("admin");
        String adminPassword = "admin";
        admin.setHashedPassword(passwordEncoder.encode(adminPassword));

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(Role.USER);
        adminRoles.add(Role.ADMIN);
        admin.setRoles(adminRoles);
        this.userRepository.save(admin);
    }
}

    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(
                        role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user present with username: " + username));
        return new org.springframework.security.core.userdetails.
                User(user.getUsername(), user.getHashedPassword(),
                getAuthorities(user));

    }
}
