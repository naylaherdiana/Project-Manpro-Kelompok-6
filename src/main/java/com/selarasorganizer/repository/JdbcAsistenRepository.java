package com.selarasorganizer.repository;

import com.selarasorganizer.model.RegisterRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAsistenRepository implements AsistenRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public JdbcAsistenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean saveAsisten(RegisterRequest request) {
        try {
            // 1. Simpan ke users
            String hashed = passwordEncoder.encode(request.getPassword());
            jdbcTemplate.update(
                "INSERT INTO users (username, email, password) VALUES (?, ?, ?)",
                request.getUsername(), request.getEmail(), hashed
            );

            // 2. Ambil user_id
            Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Long.class,
                request.getUsername()
            );

            // 3. Simpan role
            jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role) VALUES (?, 'ASISTEN')",
                userId
            );

            // 4. Simpan profil asisten
            jdbcTemplate.update(
                "INSERT INTO asisten (nama, alamat, kontak, user_id) VALUES (?, ?, ?, ?)",
                request.getNama(), request.getAlamat(), request.getKontak(), userId
            );

            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}