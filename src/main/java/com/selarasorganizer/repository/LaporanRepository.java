package com.selarasorganizer.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.selarasorganizer.model.VendorAktif;
import com.selarasorganizer.model.EventBerlangsung;

@Repository
public class LaporanRepository {
    private final JdbcTemplate jdbcTemplate;

    public LaporanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VendorAktif> findVendorPalingAktif() {
        String sql = """
            SELECT 
                v.idvendor,
                v.namavendor,
                v.namapemilik,
                COUNT(m.idvendor) as jumlah_event
            FROM vendor v
            LEFT JOIN menangani m ON v.idvendor = m.idvendor
            GROUP BY v.idvendor, v.namavendor, v.namapemilik
            ORDER BY jumlah_event DESC, v.namavendor
            LIMIT 5
            """;
        
        return jdbcTemplate.query(sql, this::mapRowToVendorAktif);
    }

    public List<EventBerlangsung> findEventBerlangsung() {
        String sql = """
            SELECT 
                e.idevent,
                e.namaevent,
                e.tanggal,
                a.nama as nama_asisten,
                k.namaklien as nama_klien
            FROM event e
            JOIN asisten a ON e.idasisten = a.id
            JOIN klien k ON e.idklien = k.idklien
            WHERE e.statusevent = 'BERLANGSUNG'
                AND e.tanggal BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days'
            ORDER BY e.tanggal ASC
            LIMIT 10
            """;
        
        return jdbcTemplate.query(sql, this::mapRowToEventBerlangsung);
    }

    private VendorAktif mapRowToVendorAktif(ResultSet rs, int rowNum) throws SQLException {
        VendorAktif vendor = new VendorAktif();
        vendor.setIdvendor(rs.getLong("idvendor"));
        vendor.setNamavendor(rs.getString("namavendor"));
        vendor.setNamapemilik(rs.getString("namapemilik"));
        vendor.setJumlahEvent(rs.getInt("jumlah_event"));
        return vendor;
    }

    private EventBerlangsung mapRowToEventBerlangsung(ResultSet rs, int rowNum) throws SQLException {
        EventBerlangsung event = new EventBerlangsung();
        event.setIdevent(rs.getLong("idevent"));
        event.setNamaevent(rs.getString("namaevent"));
        event.setTanggal(rs.getDate("tanggal").toLocalDate());
        event.setNamaAsisten(rs.getString("nama_asisten"));
        event.setNamaKlien(rs.getString("nama_klien"));
        return event;
    }
}