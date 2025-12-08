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

    public AuthService(UserRepository userRepository, UserRoleRepository userRoleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(String username, String rawPassword) {
        try {
            User user = userRepository.findByUsername(username);
            if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
                return userRoleRepository.findRoleByUserId(user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object[] authenticateWithUserId(String username, String rawPassword) {
        try {
            User user = userRepository.findByUsername(username);
            if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
                String role = userRoleRepository.findRoleByUserId(user.getId());
                if (role != null) {
                    return new Object[]{role, user.getId()};
                }
            }
        } catch (Exception e) {
            System.out.println("Auth error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}