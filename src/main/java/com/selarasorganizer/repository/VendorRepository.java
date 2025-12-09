package com.selarasorganizer.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.selarasorganizer.model.Vendor;

@Repository
public class VendorRepository{
    private final JdbcTemplate jdbcTemplate;

    public VendorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countAll() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vendor", Integer.class);
    }

    public List<Vendor> findAll(){
        String sql = """
                SELECT 
                    v.idvendor,
                    v.namapemilik,
                    v.namavendor,
                    v.alamatvendor,
                    v.kontakvendor,
                    v.idjenisvendor,
                    jv.namajenisvendor 
                FROM vendor v
                LEFT JOIN JenisVendor jv ON v.idjenisvendor = jv.idjenisvendor
                ORDER BY v.idvendor
                """;
        return jdbcTemplate.query(sql, this::mapRowToVendor);
    }

    public Vendor findById(Long id) {
        String sql = """
                SELECT 
                    v.*,
                    jv.namajenisvendor
                FROM vendor v
                LEFT JOIN JenisVendor jv ON v.idjenisvendor = jv.idjenisvendor
                WHERE v.idvendor = ?
                """;
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToVendor, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void update(Vendor vendor) {
        String sql = "UPDATE vendor SET namapemilik = ?, namavendor = ?, alamatvendor = ?, kontakvendor = ?, idjenisvendor = ? WHERE idvendor = ?";
        jdbcTemplate.update(sql,
            vendor.getNamapemilik(),
            vendor.getNamavendor(),
            vendor.getAlamatvendor(),
            vendor.getKontakvendor(),
            vendor.getIdjenisvendor(),
            vendor.getIdvendor()
        );
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM vendor WHERE idvendor = ?";
        jdbcTemplate.update(sql, id);
    }

    public void save(Vendor vendor) {
        String sql = "INSERT INTO vendor (namapemilik, namavendor, alamatvendor, kontakvendor, idjenisvendor) VALUES (?, ?, ?, ?, ?)";
        try {
            int rows = jdbcTemplate.update(sql,
                vendor.getNamapemilik(),
                vendor.getNamavendor(),
                vendor.getAlamatvendor(),
                vendor.getKontakvendor(),
                vendor.getIdjenisvendor()
            );
            System.out.println("Rows inserted: " + rows);
        } catch (Exception e) {
            System.out.println("ERROR in repository save: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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

    private Vendor mapRowToVendor(ResultSet rs, int rowNum) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setIdvendor(rs.getLong("idvendor"));
        vendor.setNamapemilik(rs.getString("namapemilik"));
        vendor.setNamavendor(rs.getString("namavendor"));
        vendor.setAlamatvendor(rs.getString("alamatvendor"));
        vendor.setKontakvendor(rs.getString("kontakvendor"));
        vendor.setIdjenisvendor(rs.getLong("idjenisvendor"));

        try {
            vendor.setNamajenisvendor(rs.getString("namajenisvendor"));
        } catch (SQLException e) {
            vendor.setNamajenisvendor("Tidak diketahui");
        }
        
        return vendor;
    }
}
