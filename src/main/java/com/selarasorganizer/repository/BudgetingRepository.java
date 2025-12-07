package com.selarasorganizer.repository;

import com.selarasorganizer.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BudgetingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BudgetingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<JenisVendor> findAllJenisVendor(BigDecimal hargaMin, BigDecimal hargaMax) {
        if (hargaMin == null && hargaMax == null) {
            String sql = "SELECT * FROM JenisVendor ORDER BY namajenisvendor";
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor);
        } else if (hargaMin != null && hargaMax != null) {
            String sql = """
                SELECT * FROM JenisVendor 
                WHERE kisaranhargamin >= ? AND kisaranhargamax <= ?
                ORDER BY namajenisvendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor, hargaMin, hargaMax);
        } else if (hargaMin != null) {
            String sql = """
                SELECT * FROM JenisVendor 
                WHERE kisaranhargamin >= ?
                ORDER BY namajenisvendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor, hargaMin);
        } else {
            String sql = """
                SELECT * FROM JenisVendor 
                WHERE kisaranhargamax <= ?
                ORDER BY namajenisvendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor, hargaMax);
        }
    }

    public List<Vendor> findVendorByJenis(Long idjenisvendor) {
        String sql = """
            SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
            FROM Vendor v
            JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
            WHERE v.idjenisvendor = ?
            ORDER BY v.namavendor
            """;
        return jdbcTemplate.query(sql, this::mapRowToVendor, idjenisvendor);
    }

    public List<Vendor> findAllVendor() {
        String sql = """
            SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
            FROM Vendor v
            JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
            ORDER BY v.namavendor
            """;
        return jdbcTemplate.query(sql, this::mapRowToVendor);
    }

    public List<Event> findEventBerlangsungByAsisten(Long asistenId) {
        String sql = """
            SELECT e.*, k.namaklien
            FROM Event e
            JOIN Klien k ON e.idklien = k.idklien
            WHERE e.idasisten = ? AND e.statusevent IN ('BERLANGSUNG', 'PERENCANAAN')
            ORDER BY e.tanggal
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Event event = new Event();
            event.setIdevent(rs.getLong("idevent"));
            event.setNamaevent(rs.getString("namaevent"));
            event.setJenisevent(rs.getString("jenisevent"));
            event.setTanggal(rs.getDate("tanggal").toLocalDate());
            event.setNamaklien(rs.getString("namaklien"));
            return event;
        }, asistenId);
    }

    public List<Menangani> findVendorByEvent(Long idevent) {
        String sql = """
            SELECT m.*, 
                   v.namavendor, 
                   e.namaevent, 
                   j.namajenisvendor,
                   jv.namajenisvendor as jenis_nama
            FROM Menangani m
            JOIN Vendor v ON m.idvendor = v.idvendor
            JOIN Event e ON m.idevent = e.idevent
            JOIN JenisVendor jv ON v.idjenisvendor = jv.idjenisvendor
            LEFT JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
            WHERE m.idevent = ?
            ORDER BY v.namavendor
            """;
        return jdbcTemplate.query(sql, this::mapRowToMenangani, idevent);
    }

    public List<Menangani> findVendorByAsisten(Long asistenId) {
        String sql = """
            SELECT m.*, v.namavendor, e.namaevent
            FROM Menangani m
            JOIN Vendor v ON m.idvendor = v.idvendor
            JOIN Event e ON m.idevent = e.idevent
            WHERE m.idasisten = ?
            ORDER BY e.tanggal DESC, v.namavendor
            """;
        return jdbcTemplate.query(sql, this::mapRowToMenangani, asistenId);
    }

    public Vendor findVendorById(Long idvendor) {
        String sql = """
            SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
            FROM Vendor v
            JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
            WHERE v.idvendor = ?
            """;
        List<Vendor> results = jdbcTemplate.query(sql, this::mapRowToVendor, idvendor);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Vendor> findVendorByJenisId(Long jenisId) {
        String sql = """
            SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
            FROM Vendor v
            JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
            WHERE v.idjenisvendor = ?
            ORDER BY v.namavendor
            """;
        return jdbcTemplate.query(sql, this::mapRowToVendor, jenisId);
    }

    public void addVendorToEvent(Menangani menangani) {
        String sql = """
            INSERT INTO Menangani (idasisten, idevent, idvendor, hargadealing, statusdealing)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        jdbcTemplate.update(sql,
            menangani.getIdasisten(),
            menangani.getIdevent(),
            menangani.getIdvendor(),
            menangani.getHargadealing(),
            menangani.getStatusdealing());
    }

    public void updateHargaDealing(Menangani menangani) {
        String sql = """
            UPDATE Menangani 
            SET hargadealing = ?, statusdealing = ?
            WHERE idevent = ? AND idvendor = ?
            """;
        
        jdbcTemplate.update(sql,
            menangani.getHargadealing(),
            menangani.getStatusdealing(),
            menangani.getIdevent(),
            menangani.getIdvendor());
    }

    public boolean removeVendorFromEvent(Long idevent, Long idvendor) {
        String sql = "DELETE FROM Menangani WHERE idevent = ? AND idvendor = ?";
        int rowsAffected = jdbcTemplate.update(sql, idevent, idvendor);
        return rowsAffected > 0;
    }

    public List<Vendor> searchVendorByHarga(BigDecimal hargaMin, BigDecimal hargaMax) {
        if (hargaMin == null && hargaMax == null) {
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor);
        } else if (hargaMin != null && hargaMax != null) {
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                WHERE j.kisaranhargamin >= ? AND j.kisaranhargamax <= ?
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor, hargaMin, hargaMax);
        } else if (hargaMin != null) {
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                WHERE j.kisaranhargamin >= ?
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor, hargaMin);
        } else {
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                WHERE j.kisaranhargamax <= ?
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor, hargaMax);
        }
    }

    public List<JenisVendor> findAllJenisVendor(Double hargaMin, Double hargaMax) {
        if (hargaMin == null && hargaMax == null) {
            // Tidak ada filter
            String sql = "SELECT * FROM JenisVendor ORDER BY namajenisvendor";
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor);
        } else if (hargaMin != null && hargaMax != null) {
            // Filter kedua-duanya
            String sql = """
                SELECT * FROM JenisVendor 
                WHERE kisaranhargamin >= ? AND kisaranhargamax <= ?
                ORDER BY namajenisvendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor, hargaMin, hargaMax);
        } else if (hargaMin != null) {
            // Filter hanya harga min
            String sql = """
                SELECT * FROM JenisVendor 
                WHERE kisaranhargamin >= ?
                ORDER BY namajenisvendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor, hargaMin);
        } else {
            // Filter hanya harga max
            String sql = """
                SELECT * FROM JenisVendor 
                WHERE kisaranhargamax <= ?
                ORDER BY namajenisvendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToJenisVendor, hargaMax);
        }
    }

    public List<Vendor> searchVendorByHarga(Double hargaMin, Double hargaMax) {
        if (hargaMin == null && hargaMax == null) {
            // Tidak ada filter
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor);
        } else if (hargaMin != null && hargaMax != null) {
            // Filter kedua-duanya
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                WHERE j.kisaranhargamin >= ? AND j.kisaranhargamax <= ?
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor, hargaMin, hargaMax);
        } else if (hargaMin != null) {
            // Filter hanya harga min
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                WHERE j.kisaranhargamin >= ?
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor, hargaMin);
        } else {
            // Filter hanya harga max
            String sql = """
                SELECT v.*, j.namajenisvendor, j.kisaranhargamin, j.kisaranhargamax
                FROM Vendor v
                JOIN JenisVendor j ON v.idjenisvendor = j.idjenisvendor
                WHERE j.kisaranhargamax <= ?
                ORDER BY v.namavendor
                """;
            return jdbcTemplate.query(sql, this::mapRowToVendor, hargaMax);
        }
    }

    private JenisVendor mapRowToJenisVendor(ResultSet rs, int rowNum) throws SQLException {
        JenisVendor jenis = new JenisVendor();
        jenis.setIdjenisvendor(rs.getLong("idjenisvendor"));
        jenis.setKisaranhargamin(rs.getBigDecimal("kisaranhargamin"));
        jenis.setKisaranhargamax(rs.getBigDecimal("kisaranhargamax"));
        jenis.setNamajenisvendor(rs.getString("namajenisvendor"));
        return jenis;
    }

    private Vendor mapRowToVendor(ResultSet rs, int rowNum) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setIdvendor(rs.getLong("idvendor"));
        vendor.setNamapemilik(rs.getString("namapemilik"));
        vendor.setNamavendor(rs.getString("namavendor"));
        vendor.setAlamatvendor(rs.getString("alamatvendor"));
        vendor.setKontakvendor(rs.getString("kontakvendor"));
        vendor.setIdjenisvendor(rs.getLong("idjenisvendor"));
        
        vendor.setNamajenisvendor(rs.getString("namajenisvendor"));
        vendor.setKisaranhargamin(rs.getBigDecimal("kisaranhargamin"));
        vendor.setKisaranhargamax(rs.getBigDecimal("kisaranhargamax"));
        
        return vendor;
    }

    private Menangani mapRowToMenangani(ResultSet rs, int rowNum) throws SQLException {
        Menangani menangani = new Menangani();
        menangani.setIdasisten(rs.getLong("idasisten"));
        menangani.setIdevent(rs.getLong("idevent"));
        menangani.setIdvendor(rs.getLong("idvendor"));
        menangani.setHargadealing(rs.getBigDecimal("hargadealing"));
        menangani.setStatusdealing(rs.getString("statusdealing"));
        menangani.setNamavendor(rs.getString("namavendor"));
        menangani.setNamaevent(rs.getString("namaevent"));
        
        String jenisNama = rs.getString("jenis_nama");
        if (jenisNama == null) {
            jenisNama = rs.getString("namajenisvendor");
        }
    
        return menangani;
    }
}