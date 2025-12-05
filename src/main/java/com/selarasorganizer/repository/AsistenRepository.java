package com.selarasorganizer.repository;

import com.selarasorganizer.model.Asisten;
import com.selarasorganizer.model.RegisterRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
        System.out.println("\n=== SIMPLE saveAsisten ===");
        
        try {
            String userSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?) RETURNING id";
            
            Long userId = jdbcTemplate.queryForObject(
                userSql,
                Long.class,
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
            );
            
            System.out.println("User ID: " + userId);
            
            jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role) VALUES (?, 'ASISTEN')",
                userId
            );
     
            jdbcTemplate.update(
                "INSERT INTO asisten (nama, alamat, kontak, user_id) VALUES (?, ?, ?, ?)",
                request.getNama(), 
                request.getAlamat(), 
                request.getKontak(), 
                userId
            );
            
            return true;
            
        } catch (DataIntegrityViolationException e) {
            System.out.println("Duplicate username/email: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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

    public Long getAsistenIdByUserId(Long userId) {
        try {
            String sql = "SELECT id FROM Asisten WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (Exception e) {
            return null;
        }
    }

    public String getNamaAsistenByUserId(Long userId) {
        try {
            String sql = """
                SELECT a.nama 
                FROM Asisten a 
                WHERE a.user_id = ?
                """;
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (Exception e) {
            return null;
        }
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