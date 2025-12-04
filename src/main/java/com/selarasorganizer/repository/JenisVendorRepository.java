package com.selarasorganizer.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.selarasorganizer.model.JenisVendor;

@Repository
public class JenisVendorRepository {
    private final JdbcTemplate jdbcTemplate;

    public JenisVendorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<JenisVendor> findAll() {
        String sql = "SELECT * FROM jenisvendor ORDER BY idjenisvendor";
        return jdbcTemplate.query(sql, this::mapRowToJenisVendor);
    }

    public JenisVendor findById(Long id) {
        String sql = "SELECT * FROM jenisvendor WHERE idjenisvendor = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToJenisVendor, id);
    }

    public void save(JenisVendor jenisVendor) {
        String sql = "INSERT INTO jenisvendor (idjenisvendor, kisaranhargamin, kisaranhargamax, namajenisvendor) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            jenisVendor.getIdjenisvendor(),
            jenisVendor.getKisaranhargamin(),
            jenisVendor.getKisaranhargamax(),
            jenisVendor.getNamajenisvendor()
        );
    }

    public void update(JenisVendor jenisVendor) {
        String sql = "UPDATE jenisvendor SET kisaranhargamin = ?, kisaranhargamax = ?, namajenisvendor = ? WHERE idjenisvendor = ?";
        jdbcTemplate.update(sql,
            jenisVendor.getKisaranhargamin(),
            jenisVendor.getKisaranhargamax(),
            jenisVendor.getNamajenisvendor(),
            jenisVendor.getIdjenisvendor()
        );
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM jenisvendor WHERE idjenisvendor = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM jenisvendor WHERE idjenisvendor = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private JenisVendor mapRowToJenisVendor(ResultSet rs, int rowNum) throws SQLException {
        JenisVendor jenisVendor = new JenisVendor();
        jenisVendor.setIdjenisvendor(rs.getLong("idjenisvendor"));
        jenisVendor.setKisaranhargamin(rs.getBigDecimal("kisaranhargamin"));
        jenisVendor.setKisaranhargamax(rs.getBigDecimal("kisaranhargamax"));
        jenisVendor.setNamajenisvendor(rs.getString("namajenisvendor"));
        return jenisVendor;
    }
}