package com.selarasorganizer.repository;

import com.selarasorganizer.model.Asisten;
import com.selarasorganizer.model.RegisterRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class AsistenRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public AsistenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<Asisten> findAll() {
        String sql = """
            SELECT 
                a.id,
                a.nama,
                a.alamat,
                a.kontak,
                u.email,        -- ambil dari users
                a.user_id
            FROM asisten a
            JOIN users u ON a.user_id = u.id
            ORDER BY a.id
            """;
        return jdbcTemplate.query(sql, this::mapRowToAsisten);
    }

    public Asisten findById(Long id) {
        String sql = "SELECT id, nama, alamat, kontak, user_id FROM asisten WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToAsisten, id);
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

    public Asisten findByIdWithEmail(Long id) {
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
    }

    public boolean saveAsisten(RegisterRequest request) {
        try {
            String hashed = passwordEncoder.encode(request.getPassword());
            jdbcTemplate.update(
                "INSERT INTO users (username, email, password) VALUES (?, ?, ?)",
                request.getUsername(), request.getEmail(), hashed
            );
            Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Long.class,
                request.getUsername()
            );

            jdbcTemplate.update(
                "INSERT INTO user_roles (user_id, role) VALUES (?, 'ASISTEN')",
                userId
            );

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