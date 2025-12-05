package com.selarasorganizer.repository;

import com.selarasorganizer.model.Klien;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class KlienRepository {

    private final JdbcTemplate jdbcTemplate;

    public KlienRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countKlienDitangani(Long asistenId) {
        String sql = """
            SELECT COUNT(DISTINCT idklien) 
            FROM Event 
            WHERE idasisten = ?
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, asistenId);
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM klien";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<Klien> findAll() {
        String sql = "SELECT * FROM klien";
        return jdbcTemplate.query(sql, this::mapRowToKlien);
    }

    private Klien mapRowToKlien(ResultSet rs, int rowNum) throws SQLException {
        Klien klien = new Klien();
        klien.setIdklien(rs.getLong("idklien"));
        klien.setNamaklien(rs.getString("namaklien"));
        klien.setAlamatklien(rs.getString("alamatklien"));
        klien.setKontakklien(rs.getString("kontakklien"));
        return klien;
    }
}