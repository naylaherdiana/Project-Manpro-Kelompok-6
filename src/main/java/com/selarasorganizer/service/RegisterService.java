package com.selarasorganizer.service;

import com.selarasorganizer.model.RegisterRequest;
import com.selarasorganizer.repository.AsistenRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final AsistenRepository asistenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegisterService(AsistenRepository asistenRepository) {
        this.asistenRepository = asistenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean registerAsisten(RegisterRequest request) {
        try {
            System.out.println("=== DEBUG REGISTER ===");
            System.out.println("Password sebelum encode: " + request.getPassword());
            
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            System.out.println("Password setelah encode: " + encodedPassword);
            System.out.println("Panjang encoded: " + encodedPassword.length());
            
            if (!encodedPassword.startsWith("$2a$") && !encodedPassword.startsWith("$2b$")) {
                System.err.println("ERROR: Password tidak di-encode dengan BCrypt!");
                return false;
            }
            
            RegisterRequest encodedRequest = new RegisterRequest();
            encodedRequest.setNama(request.getNama());
            encodedRequest.setAlamat(request.getAlamat());
            encodedRequest.setKontak(request.getKontak());
            encodedRequest.setUsername(request.getUsername());
            encodedRequest.setEmail(request.getEmail());
            encodedRequest.setPassword(encodedPassword);
            
            boolean result = asistenRepository.saveAsisten(encodedRequest);
            System.out.println("Hasil save ke database: " + result);
            return result;
            
        } catch (Exception e) {
            System.err.println("Error pada registerAsisten: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}