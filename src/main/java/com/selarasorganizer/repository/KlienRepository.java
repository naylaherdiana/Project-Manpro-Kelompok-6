package com.selarasorganizer.repository;

import com.selarasorganizer.model.Klien;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.sql.PreparedStatement;
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

    public Klien findById(Long id) {
        String sql = "SELECT * FROM klien WHERE idklien = ?";
        List<Klien> results = jdbcTemplate.query(sql, this::mapRowToKlien, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(Klien klien) {
        if (klien.getIdklien() == null) {
            String sql = "INSERT INTO klien (namaklien, alamatklien, kontakklien) VALUES (?, ?, ?)";
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"idklien"});
                ps.setString(1, klien.getNamaklien());
                ps.setString(2, klien.getAlamatklien());
                ps.setString(3, klien.getKontakklien());
                return ps;
            }, keyHolder);
            klien.setIdklien(keyHolder.getKey().longValue());
            
        } else {
            String sql = "UPDATE klien SET namaklien = ?, alamatklien = ?, kontakklien = ? WHERE idklien = ?";
            jdbcTemplate.update(sql,
                klien.getNamaklien(),
                klien.getAlamatklien(),
                klien.getKontakklien(),
                klien.getIdklien());
        }
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM klien WHERE idklien = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
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