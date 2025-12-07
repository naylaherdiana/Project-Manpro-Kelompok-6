package com.selarasorganizer.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.selarasorganizer.model.Klien;
import com.selarasorganizer.model.Event;
import com.selarasorganizer.model.EventDashboard;
import com.selarasorganizer.model.EventDashboardAsisten;

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

    public List<EventDashboard> findUpcomingEvents(){
        System.out.println("\n=== DEBUG: findUpcomingEvents FOR PEMILIK ===");

        String sql = "SELECT e.idevent, e.namaevent, e.tanggal, e.statusevent, " +
                     "k.namaklien, a.nama as nama_asisten " +
                     "FROM event e " +
                     "LEFT JOIN klien k ON e.idklien = k.idklien " +
                     "LEFT JOIN asisten a ON e.idasisten = a.id " +
                     "WHERE e.statusevent = 'BERLANGSUNG' " +
                     "ORDER BY e.tanggal ASC " +
                     "LIMIT 4";
        
        System.out.println("SQL Query: " + sql);
        
        try {
            List<EventDashboard> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                EventDashboard event = new EventDashboard();
                event.setIdevent(rs.getLong("idevent"));
                event.setNamaevent(rs.getString("namaevent"));
                
                java.sql.Date sqlDate = rs.getDate("tanggal");
                event.setTanggal(sqlDate != null ? sqlDate.toLocalDate() : LocalDate.now());
                
                event.setStatusevent(rs.getString("statusevent"));
                
                try {
                    event.setNamaKlien(rs.getString("namaklien"));
                } catch (SQLException e) {
                    event.setNamaKlien("Tidak ada klien");
                }
                
                try {
                    event.setNamaAsisten(rs.getString("nama_asisten"));
                } catch (SQLException e) {
                    event.setNamaAsisten("Tidak ada asisten");
                }
                
                return event;
            });
            
            System.out.println("Found " + results.size() + " upcoming events for Pemilik");
            
            for (EventDashboard event : results) {
                System.out.println("- ID: " + event.getIdevent() + 
                                 ", Event: " + event.getNamaevent() + 
                                 ", Klien: " + event.getNamaKlien() + 
                                 ", Asisten: " + event.getNamaAsisten());
            }
            
            return results;
        } catch (Exception e) {
            System.out.println("ERROR in findUpcomingEvents (Pemilik): " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public int countEventDitangani(Long asistenId) {
        String sql = "SELECT COUNT(*) FROM Event WHERE idasisten = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, asistenId);
    }

    public int countEventTuntas(Long asistenId) {
        String sql = """
            SELECT COUNT(*) 
            FROM Event 
            WHERE idasisten = ? AND statusevent = 'TUNTAS'
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, asistenId);
    }

    public List<EventDashboardAsisten> getEventBerlangsung(Long asistenId) {
        String sql = """
            SELECT 
                e.namaevent,
                k.namaklien,
                e.tanggal,
                a.nama as nama_asisten
            FROM Event e
            JOIN Klien k ON e.idklien = k.idklien
            JOIN Asisten a ON e.idasisten = a.id
            WHERE e.idasisten = ? 
                AND e.statusevent = 'BERLANGSUNG'
                AND e.tanggal >= CURRENT_DATE
            ORDER BY e.tanggal ASC
            LIMIT 10
            """;
        return jdbcTemplate.query(sql, this::mapRowToEventDashboardAsisten, asistenId);
    }

    public List<EventDashboard> getEventDetailBerlangsung(Long asistenId) {
        String sql = """
            SELECT e.idevent, e.namaevent, e.jenisevent, e.tanggal, 
                   e.jumlahundangan, e.statusevent, k.namaklien
            FROM Event e 
            JOIN Klien k ON e.idklien = k.idklien 
            WHERE e.idasisten = ? AND e.statusevent = 'BERLANGSUNG'
            ORDER BY e.tanggal
            """;
        return jdbcTemplate.query(sql, this::mapRowToEventDashboard, asistenId);
    }

    public List<Event> findAllByAsisten(Long asistenId) {
        String sql = """
            SELECT e.*, k.namaklien 
            FROM Event e 
            LEFT JOIN Klien k ON e.idklien = k.idklien 
            WHERE e.idasisten = ?
            ORDER BY e.tanggal DESC, e.idevent DESC
            """;
        return jdbcTemplate.query(sql, this::mapRowToEventFull, asistenId);
    }

    public List<Event> findTuntasByAsisten(Long asistenId) {
        String sql = """
            SELECT e.*, k.namaklien 
            FROM Event e 
            LEFT JOIN Klien k ON e.idklien = k.idklien 
            WHERE e.idasisten = ? AND e.statusevent = 'TUNTAS'
            ORDER BY e.tanggal DESC
            """;
        return jdbcTemplate.query(sql, this::mapRowToEventFull, asistenId);
    }

    public List<Event> findBerlangsungByAsisten(Long asistenId) {
        String sql = """
            SELECT e.*, k.namaklien 
            FROM Event e 
            LEFT JOIN Klien k ON e.idklien = k.idklien 
            WHERE e.idasisten = ? AND e.statusevent = 'BERLANGSUNG'
            ORDER BY e.tanggal
            """;
        return jdbcTemplate.query(sql, this::mapRowToEventFull, asistenId);
    }

    public Event findById(Long id) {
        String sql = """
            SELECT e.*, k.namaklien 
            FROM Event e 
            LEFT JOIN Klien k ON e.idklien = k.idklien 
            WHERE e.idevent = ?
            """;
        List<Event> results = jdbcTemplate.query(sql, this::mapRowToEventFull, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(Event event) {
        if (event.getIdevent() == null) {
            // INSERT new event
            String sql = """
                INSERT INTO Event (namaevent, jenisevent, tanggal, jumlahundangan, statusevent, idklien, idasisten) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"idevent"});
                ps.setString(1, event.getNamaevent());
                ps.setString(2, event.getJenisevent());
                ps.setDate(3, java.sql.Date.valueOf(event.getTanggal()));
                ps.setInt(4, event.getJumlahundangan());
                ps.setString(5, event.getStatusevent());
                ps.setLong(6, event.getIdklien());
                ps.setLong(7, event.getIdasisten());
                return ps;
            }, keyHolder);
            
            event.setIdevent(keyHolder.getKey().longValue());
        } else {
            // UPDATE existing event
            String sql = """
                UPDATE Event 
                SET namaevent = ?, jenisevent = ?, tanggal = ?, 
                    jumlahundangan = ?, statusevent = ?, idklien = ? 
                WHERE idevent = ?
                """;
            jdbcTemplate.update(sql,
                event.getNamaevent(),
                event.getJenisevent(),
                event.getTanggal(),
                event.getJumlahundangan(),
                event.getStatusevent(),
                event.getIdklien(),
                event.getIdevent());
        }
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Event WHERE idevent = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public List<Klien> getAllKlien() {
        String sql = "SELECT * FROM Klien ORDER BY namaklien";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            com.selarasorganizer.model.Klien klien = new com.selarasorganizer.model.Klien();
            klien.setIdklien(rs.getLong("idklien"));
            klien.setNamaklien(rs.getString("namaklien"));
            return klien;
        });
    }

    private Event mapRowToEventFull(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setIdevent(rs.getLong("idevent"));
        event.setNamaevent(rs.getString("namaevent"));
        event.setJenisevent(rs.getString("jenisevent"));
        event.setTanggal(rs.getDate("tanggal").toLocalDate());
        event.setJumlahundangan(rs.getInt("jumlahundangan"));
        event.setStatusevent(rs.getString("statusevent"));
        event.setIdklien(rs.getLong("idklien"));
        event.setIdasisten(rs.getLong("idasisten"));
    
        if (columnExists(rs, "namaklien")) {
            event.setNamaklien(rs.getString("namaklien"));
        }
        
        return event;
    }

    private boolean columnExists(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
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

    private EventDashboardAsisten mapRowToEventDashboardAsisten(ResultSet rs, int rowNum) throws SQLException {
        EventDashboardAsisten dashboard = new EventDashboardAsisten();
        dashboard.setNamaEvent(rs.getString("namaevent")); 
        dashboard.setNamaKlien(rs.getString("namaklien"));
        dashboard.setTanggal(rs.getDate("tanggal").toLocalDate());
        dashboard.setNamaAsisten(rs.getString("nama_asisten"));
        return dashboard;
    }

    private EventDashboard mapRowToEventDashboard(ResultSet rs, int rowNum) throws SQLException{
        EventDashboard event = new EventDashboard();
        event.setIdevent(rs.getLong("idevent"));
        event.setNamaevent(rs.getString("namaevent"));
        event.setTanggal(rs.getObject("tanggal", LocalDate.class));
        event.setStatusevent(rs.getString("statusevent"));
        event.setNamaKlien(rs.getString("namaklien"));
        event.setNamaAsisten(rs.getString("nama_asisten"));
        return event;
    }
}