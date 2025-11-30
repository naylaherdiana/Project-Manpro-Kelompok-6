package com.selarasorganizer.service;

import com.selarasorganizer.model.User;
import com.selarasorganizer.repository.UserRepository;
import com.selarasorganizer.repository.UserRoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String authenticate(String username, String rawPassword) {
        try {
            User user = userRepository.findByUsername(username);
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return userRoleRepository.findRoleByUserId(user.getId());
            }
        } catch (Exception e) {
            //User not found or query error → return null
        }
        return null;
    }
}