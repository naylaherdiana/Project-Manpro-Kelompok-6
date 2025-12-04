package com.selarasorganizer.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.selarasorganizer.model.Event;
import com.selarasorganizer.model.EventDashboard;

@Repository
public class EventRepository {
    private final JdbcTemplate jdbcTemplate;

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countByStatus(String status) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM event WHERE statusevent = ?", 
            Integer.class, status
        );
    }

    public List<Event> findTop4UpcomingEvents(){
        String sql = """
            SELECT idevent, namaevent, jenisevent, tanggal, jumlahundangan, statusevent, idklien, idasisten
            FROM event
            ORDER BY tanggal ASC
            LIMIT 4
            """;
        return jdbcTemplate.query(sql, this::mapRowToEvent);
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException{
        Event event = new Event();
        event.setIdevent(rs.getLong("idevent"));
        event.setNamaevent(rs.getString("namaevent"));
        event.setJenisevent(rs.getString("jenisevent"));
        event.setTanggal(rs.getObject("tanggal", LocalDate.class));
        event.setStatusevent(rs.getString("statusevent"));
        return event;
    }

    public List<EventDashboard> findUpcomingEvents(){
        String sql = """
            SELECT 
                e.idevent,
                e.namaevent,
                e.tanggal,
                e.statusevent,
                k.namaklien AS nama_klien,
                a.nama AS nama_asisten
            FROM event e
            JOIN klien k ON e.idklien = k.idklien
            JOIN asisten a ON e.idasisten = a.id
            WHERE e.statusevent = 'BERLANGSUNG'
            ORDER BY e.tanggal ASC
            LIMIT 4
            """;
        return jdbcTemplate.query(sql, this::mapRowToEventDashboard);
    }

    private EventDashboard mapRowToEventDashboard(ResultSet rs, int rowNum) throws SQLException{
        EventDashboard event = new EventDashboard();
        event.setIdevent(rs.getLong("idevent"));
        event.setNamaevent(rs.getString("namaevent"));
        event.setTanggal(rs.getObject("tanggal", LocalDate.class));
        event.setStatusevent(rs.getString("statusevent"));
        event.setNamaKlien(rs.getString("nama_klien"));
        event.setNamaAsisten(rs.getString("nama_asisten"));
        return event;
    }
}
