package com.selarasorganizer.repository;

import com.selarasorganizer.model.Asisten;
import com.selarasorganizer.model.RegisterRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AsistenRepository {

    private final JdbcTemplate jdbcTemplate;

    public AsistenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Asisten> findAll() {
        String sql = """
            SELECT 
                a.id,
                a.nama,
                a.alamat,
                a.kontak,
                u.email,        
                a.user_id
            FROM asisten a
            JOIN users u ON a.user_id = u.id
            ORDER BY a.id
            """;
        return jdbcTemplate.query(sql, this::mapRowToAsisten);
    }

    public Asisten findByIdWithEmail(Long id) {
        try {
            String sql = """
                SELECT 
                    a.id,
                    a.nama,
                    a.alamat,
                    a.kontak,
                    u.email,
                    a.user_id
                FROM asisten a
                JOIN users u ON a.user_id = u.id
                WHERE a.id = ?
                """;
            return jdbcTemplate.queryForObject(sql, this::mapRowToAsisten, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void save(Asisten asisten) {
        String sql = "INSERT INTO asisten (nama, alamat, kontak, user_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            asisten.getNama(),
            asisten.getAlamat(),
            asisten.getKontak(),
            asisten.getUserId()
        );
    }

    public void update(Asisten asisten) {
        jdbcTemplate.update(
            "UPDATE asisten SET nama = ?, alamat = ?, kontak = ? WHERE id = ?",
            asisten.getNama(),
            asisten.getAlamat(),
            asisten.getKontak(),
            asisten.getId()
        );

        jdbcTemplate.update(
            "UPDATE users SET email = ? WHERE id = ?",
            asisten.getEmail(),
            asisten.getUserId()
        );
    }

    public boolean saveAsisten(RegisterRequest request) {
        System.out.println("\n=== DEBUG saveAsisten ===");
        System.out.println("Username: " + request.getUsername());
        System.out.println("Email: " + request.getEmail());
        System.out.println("Nama: " + request.getNama());
        
        try {
            // 1. CEK apakah username sudah ada
            String checkUsernameSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            Integer countUsername = jdbcTemplate.queryForObject(checkUsernameSql, Integer.class, 
                request.getUsername());
            
            System.out.println("Username count: " + countUsername);
            
            // 2. CEK apakah email sudah ada
            String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            Integer countEmail = jdbcTemplate.queryForObject(checkEmailSql, Integer.class, 
                request.getEmail());
            
            System.out.println("Email count: " + countEmail);
            
            if ((countUsername != null && countUsername > 0) || 
                (countEmail != null && countEmail > 0)) {
                System.out.println("Username atau email sudah ada!");
                return false;
            }
            
            // 3. INSERT ke users
            System.out.println("Insert ke users...");
            String userSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            int userRows = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, request.getUsername());
                ps.setString(2, request.getEmail());
                ps.setString(3, request.getPassword());
                return ps;
            }, keyHolder);
            
            System.out.println("User rows affected: " + userRows);
            
            if (userRows == 0) {
                System.out.println("Gagal insert ke users!");
                return false;
            }
            
            Long userId = keyHolder.getKey().longValue();
            System.out.println("User ID generated: " + userId);
            
            // 4. INSERT ke user_roles
            System.out.println("Insert ke user_roles...");
            int roleRows = jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role) VALUES (?, 'ASISTEN')",
                userId
            );
            
            System.out.println("Role rows affected: " + roleRows);
            
            if (roleRows == 0) {
                System.out.println("Gagal insert ke user_roles!");
                jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
                return false;
            }
            
            // 5. INSERT ke asisten
            System.out.println("Insert ke asisten...");
            System.out.println("Nama: " + request.getNama());
            System.out.println("Alamat: " + request.getAlamat());
            System.out.println("Kontak: " + request.getKontak());
            System.out.println("User ID: " + userId);
            
            int asistenRows = jdbcTemplate.update(
                "INSERT INTO asisten (nama, alamat, kontak, user_id) VALUES (?, ?, ?, ?)",
                request.getNama(), 
                request.getAlamat(), 
                request.getKontak(), 
                userId
            );
            
            System.out.println("Asisten rows affected: " + asistenRows);
            
            if (asistenRows == 0) {
                System.out.println("GAGAL insert ke asisten!");
                // Clean up
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
                jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
                return false;
            }
            
            System.out.println("SUKSES total!");
            return true;
            
        } catch (DataIntegrityViolationException e) {
            System.out.println("DataIntegrityViolationException: " + e.getMessage());
            System.out.println("Biasanya karena constraint violation (foreign key, unique, etc)");
            return false;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void deleteById(Long id) {
        String getUserIdSql = "SELECT user_id FROM asisten WHERE id = ?";
        Long userId = jdbcTemplate.queryForObject(getUserIdSql, Long.class, id);
        jdbcTemplate.update("DELETE FROM asisten WHERE id = ?", id);
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }

    private Asisten mapRowToAsisten(ResultSet rs, int rowNum) throws SQLException {
        Asisten asisten = new Asisten();
        asisten.setId(rs.getLong("id"));
        asisten.setNama(rs.getString("nama"));
        asisten.setAlamat(rs.getString("alamat"));
        asisten.setKontak(rs.getString("kontak"));
        asisten.setEmail(rs.getString("email")); 
        asisten.setUserId(rs.getLong("user_id"));
        return asisten;
    }
}