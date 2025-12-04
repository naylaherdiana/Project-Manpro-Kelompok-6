package com.selarasorganizer.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VendorRepository{
    private final JdbcTemplate jdbcTemplate;

    public VendorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countAll() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vendor", Integer.class);
    }

    public String findMostUsedVendorName() {
        String sql = """
            SELECT v.namavendor
            FROM vendor v
            JOIN menangani m ON v.idvendor = m.idvendor
            GROUP BY v.idvendor, v.namavendor
            ORDER BY COUNT(m.idvendor) DESC
            LIMIT 1
            """;
        try {
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (EmptyResultDataAccessException e) {
            return "Belum ada data";
        }
    }
}
